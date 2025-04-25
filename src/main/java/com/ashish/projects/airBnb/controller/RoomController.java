package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.RoomDto;
import com.ashish.projects.airBnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomController {
    private final RoomService roomService;
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@RequestBody RoomDto roomDto, @PathVariable Long hotelId) {
        RoomDto roomDto1 = roomService.createNewRoom(hotelId, roomDto);
        return  new ResponseEntity<>(roomDto1, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        List<RoomDto> roomDto = roomService.getAllRoomsInHotel(hotelId);
        return  new ResponseEntity<>(roomDto, HttpStatus.OK);
    }
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId,@PathVariable Long roomId) {
        RoomDto roomDto = roomService.getRoomById(roomId);
        return ResponseEntity.ok(roomDto);
    }
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long hotelId,@PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }
}

