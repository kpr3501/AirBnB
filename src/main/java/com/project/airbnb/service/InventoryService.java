package com.project.airbnb.service;

import com.project.airbnb.dto.HotelSearchRequest;
import com.project.airbnb.dto.InventoryDto;
import com.project.airbnb.dto.UpdateInventoryRequestDto;
import com.project.airbnb.entity.Room;

import java.util.List;

import org.springframework.data.domain.Page;

import com.project.airbnb.dto.HotelPriceDto;

public interface InventoryService {
    void InitializeRoomForAYear(Room roomDto);
    void deleteAllInventories(Room room);
    Page<HotelPriceDto> searchHotels(HotelSearchRequest searchRequest);
    List<InventoryDto> getInventoryForRoom(Long roomId);
    void updateInventoryForRoom(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);

}
