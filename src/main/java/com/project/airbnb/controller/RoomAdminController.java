package com.project.airbnb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.airbnb.dto.RoomDto;
import com.project.airbnb.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom (@PathVariable Long hotelId, @RequestBody RoomDto roomDto) {

        log.info("Received request to create new room: {} ", roomDto.getType());
        RoomDto createdRoom = roomService.createNewRoom(hotelId, roomDto);
        log.info("Successfully created room of type: {} in hotel with ID: {}", roomDto.getType(), hotelId);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        log.info("Received request to fetch all rooms in hotel with ID: {}", hotelId);
        return new ResponseEntity<>(roomService.getAllRoomsInHotel(hotelId), HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        log.info("Received request to fetch room with ID: {}", roomId);
        RoomDto roomDto = roomService.getRoomById(roomId);
        log.info("Successfully fetched room with ID: {}", roomId);
        return new ResponseEntity<>(roomDto, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId) {
        log.info("Received request to delete room with ID: {}", roomId);
        roomService.deleteRoomById(roomId);
        log.info("Successfully deleted room with ID: {}", roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long hotelId, @PathVariable Long roomId, @RequestBody RoomDto roomDto) {
        log.info("Received request to update room with ID: {}", roomId);
        RoomDto updatedRoom = roomService.updateRoomById(hotelId, roomId, roomDto);
        log.info("Successfully updated room with ID: {}", roomId);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    
    
    

}
