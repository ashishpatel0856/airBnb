package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.dto.HotelInfoDto;
import com.ashish.projects.airBnb.dto.RoomDto;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.exceptions.UnAuthorisedExceptionn;
import com.ashish.projects.airBnb.repository.HotelRepository;
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
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;


    @Override
    public HotelDto CreateNewHotel(HotelDto hotelDto) {
        log.info("create a new hotel:{}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);


        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("get hotel by id:{}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if(!user.equals(hotel.getOwner())){
           throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+id);
       }


        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("update hotel by id:{}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("hotel not found with id"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+id);
        }

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+id);
        }

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedExceptionn("this user does not own this hotel with id"+hotelId);
        }
        hotel.setActive(true);

        // creating inventory for all the room for hotels
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public List<HotelDto> getAllHotels() {
        log.info("getting all hotels");
        User user = getCurrentUser();

        List<Hotel> hotels = hotelRepository.findByOwner(user);
      return hotels
                .stream()
                .map(ele -> modelMapper.map(ele,HotelDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public HotelInfoDto getHotelByInfo(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("hotel not found with id"));

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((ele -> modelMapper.map(ele, RoomDto.class)))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);

    }
}
