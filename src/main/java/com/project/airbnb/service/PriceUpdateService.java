package com.project.airbnb.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.HotelMinPrice;
import com.project.airbnb.entity.Inventory;
import com.project.airbnb.repository.HotelMinPriceRepository;
import com.project.airbnb.repository.HotelRepository;
import com.project.airbnb.repository.InventoryRepository;
import com.project.airbnb.strategy.PricingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PriceUpdateService
 {


    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelRepository hotelRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * *") // Schedule to run every hour, adjust as needed
    public void updatePrices() { 
        log.info("Updating prices for all hotels");
        int page = 0;
        int batchSize = 100; // Adjust batch size as needed
        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotelPage.isEmpty()){
                break;
            }

            hotelPage.getContent().forEach(hotel -> {
                // Logic to calculate and update prices for the hotel
                // This may involve fetching inventory data, calculating surge factors, and updating the price accordingly
                updateHotelPrices(hotel);
            });


            page++;
        }
    }

    public void updateHotelPrices(Hotel hotel) {
        log.info("Updating prices for hotel with ID: {}", hotel.getId());
        // Logic to calculate and update prices for the hotel
        // This may involve fetching inventory data, calculating surge factors, and updating the price accordingly
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        java.util.List<Inventory> inventories = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        updateInventoryPrices(inventories);
        updateHotelMinPrice(hotel, inventories,startDate, endDate);
    }

    public void updateInventoryPrices(java.util.List<Inventory> inventories) {
        log.info("Updating inventory prices for hotel with ID: {}", inventories.get(0).getHotel().getId());
        // Logic to calculate and update prices for the inventory
        // This may involve calculating surge factors based on demand and updating the price accordingly
        inventories.forEach(inventory -> {
            // Placeholder logic for price update, replace with actual surge factor calculation
            BigDecimal newPrice = pricingService.calculateDynamicPrice(inventory);
            inventory.setPrice(newPrice);
        });
        inventoryRepository.saveAll(inventories);
    }

    public void updateHotelMinPrice(Hotel hotel, java.util.List<Inventory> inventories, LocalDate startDate, LocalDate endDate) {
        log.info("Updating minimum price for hotel with ID: {}", hotel.getId());
        // Logic to calculate and update the minimum price for the hotel based on the inventory prices
        Map<LocalDate, BigDecimal> datePriceMap = inventories.stream()
            .collect(Collectors.groupingBy(Inventory::getDate, Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
        ))
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().orElse(BigDecimal.ZERO)));

        java.util.List<HotelMinPrice> hotelPrices = new ArrayList<>();
        datePriceMap.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                .orElseGet(() -> new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

}
