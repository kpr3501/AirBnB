package com.project.airbnb.strategy;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.airbnb.entity.Inventory;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPrice(Inventory inventory) {
        PricingStrategy priceStrategy = new BasePriceStrategy();
        priceStrategy = new SurgePricingStrategy(priceStrategy);
        priceStrategy = new OccupancyPricingStrategy(priceStrategy);
        priceStrategy = new UrgencyPricingStrategy(priceStrategy);  
        priceStrategy = new HolidayPricingStrategy(priceStrategy);

        return priceStrategy.calculatePrice(inventory);
    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::calculateDynamicPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
