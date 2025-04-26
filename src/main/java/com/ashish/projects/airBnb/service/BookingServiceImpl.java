package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.BookingDto;
import com.ashish.projects.airBnb.dto.BookingRequest;

import com.ashish.projects.airBnb.dto.GuestDto;
import com.ashish.projects.airBnb.entity.*;
import com.ashish.projects.airBnb.entity.enums.BookingStatus;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        for(Inventory inventory : inventoryList){
            inventory.setBookedCount(inventory.getBookedCount()+ bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //Create the booking hotel
//        User user = new User();
//        user.setId(1L);

        // calculate dynamic amount
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

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not reserved");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest =guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);

     booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);

    }

    private boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
