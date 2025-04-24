package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;

public interface HotelService {

    HotelDto CreateNewHotel(HotelDto hotelDto);
    HotelDto  getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    void deleteHotelById(Long id);
}
