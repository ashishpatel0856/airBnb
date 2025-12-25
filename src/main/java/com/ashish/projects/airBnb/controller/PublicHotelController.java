package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.dto.HotelInfoDto;
import com.ashish.projects.airBnb.dto.HotelPriceDto;
import com.ashish.projects.airBnb.dto.HotelSearchRequest;
import com.ashish.projects.airBnb.repository.HotelRepository;
import com.ashish.projects.airBnb.service.HotelService;
import com.ashish.projects.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicHotelController {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @GetMapping("/hotels")
    public List<HotelDto> getPublicHotels() {
        return hotelRepository.findByActiveTrue()
                .stream()
                .map(h -> modelMapper.map(h, HotelDto.class))
                .toList();
    }


    @GetMapping("/hotels/{hotelId}")
    public HotelInfoDto getHotelWithRooms(@PathVariable Long hotelId) {
        return hotelService.getHotelByInfo(hotelId);
    }


    @PostMapping("/hotels/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(
            @RequestBody HotelSearchRequest request
    ) {
        return ResponseEntity.ok(inventoryService.searchHotels(request));
    }


}

