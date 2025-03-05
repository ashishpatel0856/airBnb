package com.ashish.projects.VrboApp.controller;

import com.ashish.projects.VrboApp.dto.BookingDto;
import com.ashish.projects.VrboApp.dto.BookingRequest;
import com.ashish.projects.VrboApp.dto.GuestDto;
import com.ashish.projects.VrboApp.service.BookingService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")

public class HotelBookingController {

private final BookingService bookingService;

    public HotelBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/Init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable long bookingId,
                                                @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtoList));
    }

}