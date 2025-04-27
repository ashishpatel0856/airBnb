package com.ashish.projects.airBnb.strategy;

import com.ashish.projects.airBnb.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate today = LocalDate.now();
        if(inventory.getDate().isBefore(today) && inventory.getDate().isAfter(today.plusDays(7))) {
            price = price.multiply(new BigDecimal(1.15));
        }
        return price;
    }
}
