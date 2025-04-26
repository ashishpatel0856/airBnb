package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.BookingDto;
import com.ashish.projects.airBnb.dto.BookingRequest;
import com.ashish.projects.airBnb.dto.GuestDto;
import com.ashish.projects.airBnb.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList) {
            return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtoList));
    }
}
