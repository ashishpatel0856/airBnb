package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Booking;
import com.ashish.projects.airBnb.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);
    List<Booking> findByHotelAndCreatsAtBetween(Hotel hotel, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
