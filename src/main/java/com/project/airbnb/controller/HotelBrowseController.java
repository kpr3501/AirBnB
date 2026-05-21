package com.project.airbnb.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.HotelInfoDto;
import com.project.airbnb.dto.HotelPriceDto;
import com.project.airbnb.dto.HotelSearchRequest;
import com.project.airbnb.service.HotelService;
import com.project.airbnb.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest searchRequest) {
        Page<HotelPriceDto> page = inventoryService.searchHotels(searchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        HotelInfoDto hotelInfo = hotelService.getHotelInfoById(hotelId);
        if (hotelInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hotelInfo);
        
    }
}
