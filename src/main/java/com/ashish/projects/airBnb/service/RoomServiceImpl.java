package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.RoomDto;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.exceptions.UnAuthorisedExceptionn;
import com.ashish.projects.airBnb.repository.HotelRepository;
import com.ashish.projects.airBnb.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ashish.projects.airBnb.util.AppUtils.getCurrentUser;

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

        //  STEP 1: Save room FIRST
        Room savedRoom = roomRepository.save(room);
        inventoryService.initializeRoomForAYear(savedRoom);

        // STEP 2: Now create inventory
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(savedRoom);
        }

        // create inventory as  room is created and if hotel is active
if(hotel.getActive()){
    inventoryService.initializeRoomForAYear(room);
}
        return modelMapper.map(roomRepository.save(room), RoomDto.class);
    }

//    @Override
//    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
//        log.info("Creating a new room");
//
//        Hotel hotel = hotelRepository.findById(hotelId)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Hotel not found WITH HOTEL ID: " + hotelId));
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (!user.equals(hotel.getOwner())) {
//            throw new UnAuthorisedExceptionn(
//                    "this user does not own this hotel with id " + hotelId);
//        }
//
//        Room room = modelMapper.map(roomDto, Room.class);
//        room.setHotel(hotel);
//
//        // ✅ 1. Save room ONCE
//        Room savedRoom = roomRepository.save(room);
//
//        // ✅ 2. Create inventory ONCE
//        if (hotel.getActive()) {
//            inventoryService.initializeRoomForAYear(savedRoom);
//        }
//
//        // ✅ 3. Return saved room
//        return modelMapper.map(savedRoom, RoomDto.class);
//    }

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
    @Transactional
    public RoomDto deleteRoomById(long roomId) {
        log.info("Deleting room by ID");

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found WITH ID: " + roomId));

        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) {
            throw new UnAuthorisedExceptionn("this user does not own this room with id " + roomId);
        }
        inventoryService.deleteFutureInventories(room);

        roomRepository.delete(room);

        return modelMapper.map(room, RoomDto.class);
    }


    @Override
    @Transactional
    public RoomDto updateRoomByRoomId(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Updating room by ID");

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        User user = getCurrentUser();
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id " + hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room not found with id: " + roomId));
        room.setType(roomDto.getType());
        room.setBasePrice(roomDto.getBasePrice());
        room.setCapacity(roomDto.getCapacity());
        room.setPhotos(roomDto.getPhotos());
        room.setAmenities(roomDto.getAmenities());

        if (roomDto.getTotalCount() != null) {
            room.setTotalCount(roomDto.getTotalCount());
        }

        return modelMapper.map(room, RoomDto.class);
    }


}
