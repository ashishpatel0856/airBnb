package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.repository.HotelRepository;
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
    public void deleteHotelById(Long id){
        log.info("delete hotel by id:{}",id);
        boolean exists = hotelRepository.existsById(id);
        if(!exists)
            throw new ResourceNotFoundException("hotel not found with id:"+id);

        hotelRepository.deleteById(id);


    }
}
