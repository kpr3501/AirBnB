package com.project.airbnb.strategy;

import java.math.BigDecimal;

import com.project.airbnb.entity.Inventory;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
