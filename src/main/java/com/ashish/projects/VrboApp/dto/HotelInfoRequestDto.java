package com.ashish.projects.VrboApp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelInfoRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private String roomsCount;
}
