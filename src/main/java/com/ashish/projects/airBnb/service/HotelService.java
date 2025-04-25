package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;

import java.util.List;

public interface HotelService {

    HotelDto CreateNewHotel(HotelDto hotelDto);
    HotelDto  getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    void deleteHotelById(Long id);
    void activateHotel(Long hotelId);
    List<HotelDto> getAllHotels();
}
