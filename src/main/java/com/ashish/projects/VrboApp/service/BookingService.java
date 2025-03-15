package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.dto.BookingDto;
import com.ashish.projects.VrboApp.dto.BookingRequest;
import com.ashish.projects.VrboApp.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);
}
