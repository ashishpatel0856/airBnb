package com.ashish.projects.airBnb.strategy;

import com.ashish.projects.airBnb.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PriceService {
    public BigDecimal calculateDynamicPricing(Inventory inventory) {

        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //  additional strategies
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
        return pricingStrategy.calculatePrice(inventory);
    }

    // summing of price of inventory
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
      return   inventoryList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
