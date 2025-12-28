package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.*;

import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Inventory;
import com.ashish.projects.airBnb.entity.Room;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.repository.HotelMinPriceRepository;
import com.ashish.projects.airBnb.repository.InventoryRepository;
import com.ashish.projects.airBnb.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ashish.projects.airBnb.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j

public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;


    @Override
    public void initializeRoomForAYear(Room room) {



        if (inventoryRepository.existsByRoom(room)) {
            log.info("Inventory already exists for roomId={}, skipping", room.getId());
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for (; !today.isAfter(endDate); today = today.plusDays(1)) {


            if (inventoryRepository.existsByRoomAndDate(room, today)) {
                continue;
            }

         Inventory inventory =Inventory.builder()
                 .hotel(room.getHotel())
                 .room(room)
                 .bookedCount(0)
                 .reservedCount(0)
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
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) throws AccessDeniedException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(" rooms not found"));
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("you are not owner of this room");
        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map((element) -> modelMapper.map(element,InventoryDto.class))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) throws AccessDeniedException {
        log.info("updating all inventory by room for room ");
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("room not found with id"));

        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner()))
            throw new AccessDeniedException("you are not user for this rooom");
inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());
        inventoryRepository.updateInventory(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor()
        );
    }




    @Override
    public List<HotelPriceResponseDto> searchHotels(HotelSearchRequest request) {

        long dateCount =
                ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()
                ) + 1;

        List<Object[]> rows =
                inventoryRepository.findAvailableRooms(
                        request.getCity(),
                        request.getStartDate(),
                        request.getEndDate(),
                        dateCount,
                        request.getRoomsCount()
                );

        Map<Long, HotelPriceResponseDto> hotelMap = new LinkedHashMap<>();

        for (Object[] row : rows) {

            Long roomId = (Long) row[0];
            Double price = ((Number) row[1]).doubleValue();
            Integer availableRooms = ((Number) row[2]).intValue();

            Room room = roomRepository.findById(roomId)
                    .orElseThrow();

            Hotel hotel = room.getHotel();

            hotelMap.putIfAbsent(
                    hotel.getId(),
                    buildHotelDto(hotel)
            );

            hotelMap.get(hotel.getId())
                    .getRooms()
                    .add(buildRoomDto(room, price, availableRooms));
        }

        return new ArrayList<>(hotelMap.values());
    }



    private HotelPriceResponseDto buildHotelDto(Hotel hotel) {
        return new HotelPriceResponseDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getPhotos(),
                hotel.getAmenities(),
                hotel.getContactInfo(),
                null,
                new ArrayList<>()
        );
    }

    private RoomPriceResponseDto buildRoomDto(
            Room room,
            Double price,
            Integer availableRooms
    ) {
        return new RoomPriceResponseDto(
                room.getId(),
                room.getType(),
                room.getPhotos(),
                room.getAmenities(),
                price,
                availableRooms,
                room.getCapacity()
        );
    }

}
