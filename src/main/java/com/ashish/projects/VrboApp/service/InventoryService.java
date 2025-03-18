package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.dto.HotelPriceResponseDto;
import com.ashish.projects.VrboApp.dto.HotelSearchRequest;
import com.ashish.projects.VrboApp.dto.InventoryDto;
import com.ashish.projects.VrboApp.dto.UpdateInventoryRequestDto;
import com.ashish.projects.VrboApp.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}

