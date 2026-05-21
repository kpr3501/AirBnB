package com.project.airbnb.strategy;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;

import com.project.airbnb.entity.Inventory;

@Primary
public class BasePriceStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getPrice();
    }

}
