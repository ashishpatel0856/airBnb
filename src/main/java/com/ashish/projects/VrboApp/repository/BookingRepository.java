package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
