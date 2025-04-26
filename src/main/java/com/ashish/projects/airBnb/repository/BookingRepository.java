package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
