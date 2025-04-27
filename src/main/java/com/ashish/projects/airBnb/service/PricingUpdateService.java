package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.HotelMinPrice;
import com.ashish.projects.airBnb.entity.Inventory;
import com.ashish.projects.airBnb.repository.HotelMinPriceRepository;
import com.ashish.projects.airBnb.repository.HotelRepository;
import com.ashish.projects.airBnb.repository.InventoryRepository;
import com.ashish.projects.airBnb.strategy.PriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {
    // to update the inventory and hotelminprice tables every hour
     private final HotelRepository hotelRepository;
     private final InventoryRepository inventoryRepository;
     private final HotelMinPriceRepository hotelMinPriceRepository;
     private final PriceService priceService;

     @Scheduled(cron = "0 0 */6 * * * ")
     public void updatePrices(){
         int page = 0;
         int batchSize = 100;

         while(true){
             Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
             if(hotelPage.isEmpty()){
                 break;
             }
             hotelPage.getContent().forEach(this::updateHotelPrices);
             page++;
         }
     }

    private void updateHotelPrices(Hotel hotel) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);
        updateInventoryPrices(inventoryList);
        updateHotelPrices(hotel,inventoryList,startDate,endDate);
    }

    private void updateHotelPrices(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {

         // compute minimum price per day fot hotels
        Map<LocalDate,BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice,Collectors.minBy(Comparator.naturalOrder()))

                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,e ->e.getValue().orElse(BigDecimal.ZERO)));


        //prepare hotelprice entites in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date,price )-> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMinPrice(hotel,date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });
        // save all hotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
         inventoryList.forEach(inventory -> {
             BigDecimal dynamicPrice = priceService.calculateDynamicPricing(inventory);
             inventory.setPrice(dynamicPrice);
         });
         inventoryRepository.saveAll(inventoryList);
    }

}
