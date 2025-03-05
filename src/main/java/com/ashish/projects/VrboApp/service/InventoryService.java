package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.dto.HotelDto;
import com.ashish.projects.VrboApp.dto.HotelSearchRequest;
import com.ashish.projects.VrboApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
