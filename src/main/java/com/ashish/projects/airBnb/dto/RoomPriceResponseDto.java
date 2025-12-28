package com.ashish.projects.airBnb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class RoomPriceResponseDto {
    private Long id;
    private String type;
    private String[] photos;
    private String[] amenities;
    private Double price;
    private Integer availableRooms;
    private Integer capacity;

    public RoomPriceResponseDto(Long id, String type, String[] photos, String[] amenities, Double price, Integer availableRooms, Integer capacity) {
        this.id = id;
        this.type = type;
        this.photos = photos;
        this.amenities = amenities;
        this.price = price;
        this.availableRooms = availableRooms;
        this.capacity = capacity;
    }
//
//
//    public RoomPriceResponseDto(Long id, String type, String[] photos, String[] amenities, Double price) {
//        this.id = id;
//        this.type = type;
//        this.photos = photos;
//        this.amenities = amenities;
//        this.price = price;
//        this.availableRooms = availableRooms;
//        this.capacity = capacity;
//    }
//
//    public RoomPriceResponseDto(Long id, String type, String[] photos, String[] amenities, Double price, Integer availableRooms, Integer capacity) {
//    }
}
