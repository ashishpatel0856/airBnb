package com.ashish.projects.VrboApp.controller;

import com.ashish.projects.VrboApp.dto.HotelDto;
import com.ashish.projects.VrboApp.entity.Hotel;
import com.ashish.projects.VrboApp.service.HotelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")


public class HotelController {

    private final HotelService hotelService;

    private static final Logger log = LoggerFactory.getLogger(HotelController.class);

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }


    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto) {
        log.info("attempting to create a new hotel with id"+hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return  new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }




    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId, @RequestBody HotelDto hotelDto) {
            HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
            return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<HotelDto> deleteHotelById(@PathVariable Long hotelId) {
         hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
