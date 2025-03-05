package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.dto.HotelDto;
import com.ashish.projects.VrboApp.dto.HotelInfoDto;
import com.ashish.projects.VrboApp.entity.Hotel;

import java.util.List;


public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    Boolean deleteHotelById(Long id);
    void activateHotel(Long hotelId);

    List<HotelDto> getAllHotels();


    HotelInfoDto getHotelInfoById(Long hotelId);
}


