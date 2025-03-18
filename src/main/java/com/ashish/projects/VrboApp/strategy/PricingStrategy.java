package com.ashish.projects.VrboApp.strategy;

import com.ashish.projects.VrboApp.entity.Inventory;


import java.math.BigDecimal;


public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
