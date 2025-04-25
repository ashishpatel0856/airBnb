package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.repository.HotelRepository;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;


    @Override
    public HotelDto CreateNewHotel(HotelDto hotelDto) {
        log.info("create a new hotel:{}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("get hotel by id:{}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id"));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("update hotel by id:{}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id"));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id){
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id"));
        hotelRepository.deleteById(id);

        for(Room room: hotel.getRooms()){
            inventoryService.deleteFutureInventories(room);
        }
// delete the future inventory for hotel

    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("activating the hotel with id",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id:"+hotelId));
        hotel.setActive(true);

        // creating inventory for all the room for hotels
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

    }
}
