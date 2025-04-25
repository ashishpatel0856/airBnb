package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.HotelDto;
import com.ashish.projects.airBnb.dto.HotelSearchRequest;
import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Inventory;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j

public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;


    @Override
    public void initializeRoomForAYear(Room room) {
     LocalDate today = LocalDate.now();
     LocalDate endDate = today.plusYears(1);
     for(; !today.isAfter(endDate); today=today.plusDays(1)) {
         Inventory inventory =Inventory.builder()
                 .hotel(room.getHotel())
                 .room(room)
                 .bookedCount(0)
                 .city(room.getHotel().getCity())
                 .date(today)
                 .price(room.getBasePrice())
                 .surgeFactor(BigDecimal.ONE)
                 .totalCount(room.getTotalCount())
                 .closed(false)
                 .build();
         inventoryRepository.save(inventory);
     }
    }

    @Override
    public void deleteFutureInventories(Room room) {
        LocalDate today = LocalDate.now();
        inventoryRepository.deleteByDateAfterAndRoom(today, room);


    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        Long dateCount =
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) +1;
       Page<Hotel> hotelPage= inventoryRepository.findHotelswithAvailableRoom(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
        dateCount,pageable);
        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
    }
}
