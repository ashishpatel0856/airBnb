package com.ashish.projects.VrboApp.strategy;

import com.ashish.projects.VrboApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class BasePriceStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBaseprice();
    }
}
