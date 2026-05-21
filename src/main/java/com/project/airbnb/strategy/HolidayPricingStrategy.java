package com.project.airbnb.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;


import com.project.airbnb.entity.Inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy priceStrategy;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal basePrice = priceStrategy.calculatePrice(inventory);
        // Implement holiday-based pricing logic here, e.g., increase price during holidays
        if (isHoliday(inventory.getDate())) {
            return basePrice.multiply(BigDecimal.valueOf(1.25)); // Increase price by 25% during holidays
        } else {
            return basePrice; // No change in price if it's not a holiday
        }
    }

    private boolean isHoliday(LocalDate date) {

        return true; // Placeholder implementation
    }

}
