package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.RoomDto;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.exceptions.UnAuthorisedExceptionn;
import com.ashish.projects.airBnb.repository.HotelRepository;
import com.ashish.projects.airBnb.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;


    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room");

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found WITH HOTEL ID: " + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+hotelId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);

        // create inventory as  room is created and if hotel is active
if(hotel.getActive()){
    inventoryService.initializeRoomForAYear(room);
}
        return modelMapper.map(roomRepository.save(room), RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(long hotelId) {
        log.info("Getting all the rooms in hotel");
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found WITH ID: " + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+hotelId);
        }


        return  hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element,RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(long roomId) {
        log.info("Getting room by ID");
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found WITH ID: " + roomId));
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public RoomDto deleteRoomById(long roomId) {
        log.info("Deleting room by ID");
//        boolean exists = roomRepository.existsById(roomId);
//        if (!exists) {
//            throw new ResourceNotFoundException("Room not found WITH ID: " + roomId);
//        }

        //delete all future inventory for this rooms

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found WITH ID: " + roomId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // ✅ This will work
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this room with id"+roomId);
        }


        roomRepository.deleteById(roomId);
        inventoryService.deleteFutureInventories(room);
        return modelMapper.map(room,RoomDto.class);
    }
}
