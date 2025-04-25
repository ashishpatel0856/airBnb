package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.RoomDto;

import java.util.List;

public interface RoomService {


    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(long hotelId);
    RoomDto getRoomById(long roomId );
    RoomDto deleteRoomById(long roomId);
}
