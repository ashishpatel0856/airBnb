package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.BookingDto;
import com.ashish.projects.airBnb.dto.BookingRequest;

import com.ashish.projects.airBnb.dto.GuestDto;
import com.ashish.projects.airBnb.dto.HotelReportDto;
import com.ashish.projects.airBnb.entity.*;
import com.ashish.projects.airBnb.entity.enums.BookingStatus;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.exceptions.UnAuthorisedExceptionn;
import com.ashish.projects.airBnb.repository.*;
import com.ashish.projects.airBnb.strategy.PriceService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.ashish.projects.airBnb.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
   private final PriceService priceService;

    @Value("${frontend.url}")
    private String frontendUrl;
    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("initialiseBooking for hotel room start to end date");
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id"));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id"));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw new ResourceNotFoundException("room is not available anymore");
        }

// reserve the room ya update the booked count of inventories
//        for(Inventory inventory : inventoryList){
//            inventory.setBookedCount(inventory.getBookedCount()+ bookingRequest.getRoomsCount());
//        }
//
//        inventoryRepository.saveAll(inventoryList);

        inventoryRepository.initBooking(room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount());

        //Create the booking hotel
//        User user = new User();
//        user.setId(1L);

        // calculate dynamic amount
        BigDecimal priceForOneRoom = priceService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));


        Booking booking  = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

    booking = bookingRepository.save(booking);
    return modelMapper.map(booking, BookingDto.class);

    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("adding guests for booking with id");

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id"));




        User user = getCurrentUser();
        if(hasBookingExpired(booking)){
            throw new ResourceNotFoundException("Booking has already expired");
        }
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedExceptionn("booking does not belong to this user with id " );
        }




        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not reserved");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest =guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);

     booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);

    }

    // for payments
    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedExceptionn("booking does not belong to this user");

        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }
        // how to payment create
        String sessionUrl = checkoutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.CONFIRMED);
         bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("BOOKING IS NOT FOUND FOR SESSION ID"+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockAvailableInventory(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckInDate(),
                    booking.getRoomsCount());


            inventoryRepository.confirmBooking(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckInDate(),
                    booking.getRoomsCount());
            log.info("successfully booking with id ");

        } else {
            log.warn("not handled event",event.getType());
        }

    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedExceptionn("booking does not belong to this user");

        }
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Booking is not confirmed");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockAvailableInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckInDate(),
                booking.getRoomsCount());


        inventoryRepository.confirmBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckInDate(),
                booking.getRoomsCount());
        log.info("successfully cancelled booking ");


        // refunding payments of user
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())

                    .build();
            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }



    // checking status of hotel bookings
    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedExceptionn("booking does not belong to this user");

        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }
        return booking.getBookingStatus().name();
    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("HOTEL ID NOT FOUND"));
        User user = getCurrentUser();

        if(!user.equals(hotel.getOwner())) throw new UnAuthorisedExceptionn("you are not hotel owner");
        List<Booking> bookings = bookingRepository.findByHotel(hotel);
        return bookings
                .stream()
                .map(ele -> modelMapper.map(ele,BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        return null;
    }


//    @Override
//    @Transactional
//    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
//        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("HOTEL ID NOT FOUND"));
//        User user = getCurrentUser();
//
//        if(!user.equals(hotel.getOwner())) throw new UnAuthorisedExceptionn("you are not hotel owner");
//         LocalDateTime startDateTime = startDate.atStartOfDay();
//         LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
//         List<Booking> bookings = bookingRepository.findByHotelAndCreateAtBetween(hotel, startDateTime, endDateTime);
//         Long totalConfirmedBookings = bookings
//                 .stream()
//                 .filter(ele -> ele.getBookingStatus() == BookingStatus.CONFIRMED)
//                 .count();
//         BigDecimal totalRevenueOfconfirmedbookins = bookings
//                 .stream()
//                 .filter((ele ->ele.getBookingStatus() == BookingStatus.CONFIRMED))
//                 .map(Booking::getAmount)
//                 .reduce(BigDecimal.ZERO, BigDecimal::add);
//         BigDecimal avgRevenue = totalConfirmedBookings ==0? BigDecimal.ZERO :
//                 totalRevenueOfconfirmedbookins.divide(BigDecimal.valueOf(totalConfirmedBookings) , RoundingMode.HALF_DOWN);
//        return new HotelReportDto(totalConfirmedBookings,totalRevenueOfconfirmedbookins,avgRevenue);
//    }

    @Override
    @Transactional
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();
        return bookingRepository.findByUser(user)
                .stream()
                .map((ele) -> modelMapper.map(ele,BookingDto.class))
                .collect(Collectors.toList());

    }


    private boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

//    public User getCurrentUser() {
//        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }
}
