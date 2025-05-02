package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.BookingDto;
import com.ashish.projects.airBnb.dto.BookingRequest;
import com.ashish.projects.airBnb.dto.GuestDto;
import com.ashish.projects.airBnb.dto.HotelReportDto;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDto);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
}
