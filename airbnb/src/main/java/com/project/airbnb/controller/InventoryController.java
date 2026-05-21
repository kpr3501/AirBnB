package com.project.airbnb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.InventoryDto;
import com.project.airbnb.dto.UpdateInventoryRequestDto;
import com.project.airbnb.service.InventoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getInventoryForRoom(@PathVariable Long roomId) {
        List<InventoryDto> inventoryDtos = inventoryService.getInventoryForRoom(roomId);
        return ResponseEntity.ok(inventoryDtos);
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventoryForRoom(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        // Implement logic to update inventory for the specified room
        inventoryService.updateInventoryForRoom(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
    

}
