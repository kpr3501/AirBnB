package com.project.airbnb.strategy;

import java.math.BigDecimal;
import java.time.LocalDate;


import com.project.airbnb.entity.Inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy priceStrategy;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal basePrice = priceStrategy.calculatePrice(inventory);
        // Implement urgency-based pricing logic here, e.g., increase price as the date approaches
        long daysUntilBooking = inventory.getDate().toEpochDay() - LocalDate.now().toEpochDay();
        if (!inventory.getDate().isBefore(LocalDate.now()) && daysUntilBooking <= 3) {
            return basePrice.multiply(BigDecimal.valueOf(1.3)); // Increase price by 30% if booking is within 3 days
        } else if ( !inventory.getDate().isBefore(LocalDate.now()) && daysUntilBooking <= 7 ) {
            return basePrice.multiply(BigDecimal.valueOf(1.15)); // Increase price by 15% if booking is within 7 days
        } else {
            return basePrice; // No change in price if booking is more than 7 days away         
        }
    }
    
}
