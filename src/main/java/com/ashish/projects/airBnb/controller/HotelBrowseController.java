package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.dto.HotelInfoDto;
import com.ashish.projects.airBnb.dto.HotelSearchRequest;
import com.ashish.projects.airBnb.service.HotelService;
import com.ashish.projects.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;
    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelByInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelByInfo(hotelId));
    }
}
