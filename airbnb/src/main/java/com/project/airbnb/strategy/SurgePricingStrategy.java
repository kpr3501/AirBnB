package com.project.airbnb.strategy;

import java.math.BigDecimal;


import com.project.airbnb.entity.Inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy priceStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal basePrice = priceStrategy.calculatePrice(inventory);
        // Implement surge pricing logic here, e.g., apply discounts or surge pricing
        return basePrice.multiply(inventory.getSurgeFactor()); // For now, just return the base price
    }

}
