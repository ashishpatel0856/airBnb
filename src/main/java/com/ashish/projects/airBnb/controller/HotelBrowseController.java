package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.*;
import com.ashish.projects.airBnb.service.HotelService;
import com.ashish.projects.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;
    @PostMapping("/search-hotels")
    public ResponseEntity<List<HotelPriceResponseDto>> searchHotels(
            @RequestBody HotelSearchRequest request) {

        return ResponseEntity.ok(inventoryService.searchHotels(request));
    }




    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelByInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelByInfo(hotelId));
    }
}
