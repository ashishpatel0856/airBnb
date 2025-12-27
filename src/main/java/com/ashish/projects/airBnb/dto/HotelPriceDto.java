package com.ashish.projects.airBnb.dto;

import com.ashish.projects.airBnb.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class HotelPriceDto {

    private Long hotelId;
    private String hotelName;
    private String city;
    private BigDecimal price;

    public HotelPriceDto(Hotel hotel, BigDecimal price) {
        this.hotelId = hotel.getId();
        this.hotelName = hotel.getName();
        this.city = hotel.getCity();
        this.price = price;
    }

    public HotelPriceDto(Hotel hotel, Double avgPrice) {
        this.hotelId = hotel.getId();
        this.hotelName = hotel.getName();
        this.city = hotel.getCity();
        this.price = BigDecimal.valueOf(avgPrice);
    }

}
