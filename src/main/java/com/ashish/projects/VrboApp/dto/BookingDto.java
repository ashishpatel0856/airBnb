package com.ashish.projects.VrboApp.dto;

import com.ashish.projects.VrboApp.entity.Guest;
import com.ashish.projects.VrboApp.entity.Hotel;
import com.ashish.projects.VrboApp.entity.Room;
import com.ashish.projects.VrboApp.entity.enums.BookingStatus;
import lombok.Data;
import org.apache.catalina.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private Long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;



}
