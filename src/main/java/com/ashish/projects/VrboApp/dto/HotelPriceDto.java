package com.ashish.projects.VrboApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data


public class HotelPriceDto {
    private HotelDto hotel;
    private Double price;

    public HotelPriceDto(HotelDto hotel, Double price) {
        this.hotel = hotel;
        this.price = price;
    }
}
