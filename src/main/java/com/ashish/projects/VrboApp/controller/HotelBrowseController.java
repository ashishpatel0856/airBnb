package com.ashish.projects.VrboApp.controller;


import com.ashish.projects.VrboApp.dto.HotelDto;
import com.ashish.projects.VrboApp.dto.HotelInfoDto;
import com.ashish.projects.VrboApp.dto.HotelSearchRequest;
import com.ashish.projects.VrboApp.service.HotelService;
import com.ashish.projects.VrboApp.service.HotelServiceImpl;
import com.ashish.projects.VrboApp.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {

private final InventoryService inventoryService;
 private final HotelService hotelService;

    public HotelBrowseController(InventoryService inventoryService, HotelService hotelService, HotelServiceImpl hotelServiceImpl) {
        this.inventoryService = inventoryService;
        this.hotelService = hotelService;
    }


    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
           Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
           return ResponseEntity.ok(page);
    }


    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
