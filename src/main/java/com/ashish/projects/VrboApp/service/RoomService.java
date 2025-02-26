package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
}
