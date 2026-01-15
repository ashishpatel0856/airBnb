package com.ashish.projects.airBnb.strategy;

import com.ashish.projects.airBnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = true; // call an API or check with local data
        if (isTodayHoliday) {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
