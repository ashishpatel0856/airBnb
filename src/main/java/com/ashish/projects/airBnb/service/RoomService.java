package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.RoomDto;

public interface RoomService {


    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);
    RoomDto getAllRoomsInHotel(long hotelId);
    RoomDto getRoomById(long roomId );
    RoomDto deleteRoomById(long roomId);
}
