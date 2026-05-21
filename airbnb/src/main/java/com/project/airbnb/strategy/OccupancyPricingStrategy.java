package com.project.airbnb.strategy;

import java.math.BigDecimal;


import com.project.airbnb.entity.Inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy priceStrategy;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal basePrice = priceStrategy.calculatePrice(inventory);
        // Implement occupancy-based pricing logic here, e.g., increase price as occupancy increases
        int totalCount = inventory.getTotalCount();
        int bookedCount = inventory.getBookedCount();
        if (totalCount == 0) {
            return basePrice; // Avoid division by zero
        }
        double occupancyRate = (double) bookedCount / totalCount;
        if (occupancyRate > 0.8) {
            return basePrice.multiply(BigDecimal.valueOf(1.2)); // Increase price by 20% if occupancy is above 80%
        } else if (occupancyRate > 0.5) {
            return basePrice.multiply(BigDecimal.valueOf(1.1)); // Increase price by 10% if occupancy is above 50%
        } else {
            return basePrice; // No change in price if occupancy is 50% or below
        }
    }

}
