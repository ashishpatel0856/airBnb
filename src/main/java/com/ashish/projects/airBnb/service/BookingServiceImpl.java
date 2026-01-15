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
import com.ashish.projects.airBnb.strategy.PricingService;
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

import org.springframework.security.access.AccessDeniedException;
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
   private final PricingService priceService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found with id: "+bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = priceService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }



    @Override
    @org.springframework.transaction.annotation.Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedExceptionn("Booking does not belong to this user with id: "+user.getId());
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontendUrl+"/payments/" +bookingId +"/status",
                frontendUrl+"/payments/" +bookingId +"/status");

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedExceptionn("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        // handle the refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("adding guests for booking with id");

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id"));


        User user = getCurrentUser();
        if (hasBookingExpired(booking)) {
            throw new ResourceNotFoundException("Booking has already expired");
        }
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedExceptionn("booking does not belong to this user with id ");
        }


        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking is expired");
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not reserved");
        }

        for (GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest.setBooking(booking);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);

// ---- DTO mapping ----
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        bookingDto.setGuests(
                booking.getGuests().stream().map(g -> {
                    GuestDto dto = new GuestDto();
                    dto.setId(g.getId());
                    dto.setName(g.getName());
                    dto.setGender(g.getGender());
                    dto.setDateOfBirth(g.getDateOfBirth());
                    dto.setUserId(g.getUser().getId());
                    dto.setUserName(g.getUser().getName());
                    return dto;
                }).collect(Collectors.toSet())
        );

        return bookingDto;
    }

        // for payments
//    @Override
//    @Transactional
//    public String initiatePayment(Long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id"));
//
//        User user = getCurrentUser();
//        if(!user.equals(booking.getUser())){
//            throw new UnAuthorisedExceptionn("booking does not belong to this user");
//
//        }
//        if(hasBookingExpired(booking)){
//            throw new IllegalStateException("Booking is expired");
//        }
//        // how to payment create
//        String sessionUrl = checkoutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure");
//
//        booking.setBookingStatus(BookingStatus.CONFIRMED);
//         bookingRepository.save(booking);
//        return sessionUrl;
//    }

//    @Override
//    @Transactional
//    public void capturePayment(Event event) {
//        if("checkout.session.completed".equals(event.getType())){
//            Session session = (Session) event.getDataObjectDeserializer()
//                    .getObject().orElse(null);
//            if(session == null) return;
//
//            String sessionId = session.getId();
//            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("BOOKING IS NOT FOUND FOR SESSION ID"+sessionId));
//
//            booking.setBookingStatus(BookingStatus.CONFIRMED);
//            bookingRepository.save(booking);
//
//            inventoryRepository.findAndLockAvailableInventory(
//                    booking.getRoom().getId(),
//                    booking.getCheckInDate(),
//                    booking.getCheckInDate(),
//                    booking.getRoomsCount());
//
//
//            inventoryRepository.confirmBooking(
//                    booking.getRoom().getId(),
//                    booking.getCheckInDate(),
//                    booking.getCheckInDate(),
//                    booking.getRoomsCount());
//            log.info("successfully booking with id ");
//
//        } else {
//            log.warn("not handled event",event.getType());
//        }
//
//    }
//
//    @Override
//    @Transactional
//    public void cancelBooking(Long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
//                ()-> new ResourceNotFoundException("Booking not found with id"));
//
//        User user = getCurrentUser();
//        if(!user.equals(booking.getUser())){
//            throw new UnAuthorisedExceptionn("booking does not belong to this user");
//
//        }
//        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
//            throw new IllegalStateException("Booking is not confirmed");
//        }
//        booking.setBookingStatus(BookingStatus.CANCELLED);
//        bookingRepository.save(booking);
//
//        inventoryRepository.findAndLockAvailableInventory(
//                booking.getRoom().getId(),
//                booking.getCheckInDate(),
//                booking.getCheckInDate(),
//                booking.getRoomsCount());
//
//
//        inventoryRepository.confirmBooking(
//                booking.getRoom().getId(),
//                booking.getCheckInDate(),
//                booking.getCheckInDate(),
//                booking.getRoomsCount());
//        log.info("successfully cancelled booking ");
//
//
//        // refunding payments of user
//        try {
//            Session session = Session.retrieve(booking.getPaymentSessionId());
//            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
//                    .setPaymentIntent(session.getPaymentIntent())
//
//                    .build();
//            Refund.create(refundCreateParams);
//        } catch (StripeException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    // checking status of hotel bookings
//    @Override
//    public String getBookingStatus(Long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id"));
//
//        User user = getCurrentUser();
//        if(!user.equals(booking.getUser())){
//            throw new UnAuthorisedExceptionn("booking does not belong to this user");
//
//        }
//        if(hasBookingExpired(booking)){
//            throw new IllegalStateException("Booking is expired");
//        }
//        return booking.getBookingStatus().name();
//    }
//
//    @Override
//    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
//        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("HOTEL ID NOT FOUND"));
//        User user = getCurrentUser();
//
//        if(!user.equals(hotel.getOwner())) throw new UnAuthorisedExceptionn("you are not hotel owner");
//        List<Booking> bookings = bookingRepository.findByHotel(hotel);
//        return bookings
//                .stream()
//                .map(ele -> modelMapper.map(ele,BookingDto.class))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
//        return null;
//    }


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

//    @Override
//    @Transactional
//    public List<BookingDto> getMyBookings() {
//        User user = getCurrentUser();
//        return bookingRepository.findByUser(user)
//                .stream()
//                .map((ele) -> modelMapper.map(ele,BookingDto.class))
//                .collect(Collectors.toList());
//
//    }
//
//
//    private boolean hasBookingExpired(Booking booking) {
//        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
//    }

//    public User getCurrentUser() {
//        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }


    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedExceptionn("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus();
    }

    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Getting all booking for the hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+hotelId));
        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        Long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmedBookings, totalRevenueOfConfirmedBookings, avgRevenue);
    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();

        return bookingRepository.findByUser(user)
                .stream().
                map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
