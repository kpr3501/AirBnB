package com.project.airbnb.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.airbnb.dto.RoomDto;
import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.Room;
import com.project.airbnb.entity.User;
import com.project.airbnb.exception.ResourceNotFoundException;
import com.project.airbnb.exception.UnauthorizedException;
import com.project.airbnb.repository.HotelRepository;
import com.project.airbnb.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository  hotelRepository;
    private final InventoryService inventoryService;    

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating new room in hotel with ID: {}", hotelId);
                log.info("Fetching hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", hotelId);
            return new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        });
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        
        // Always initialize inventory for the new room for a year
        log.info("Initializing inventory for room with ID: {} in hotel with ID: {}", savedRoom.getId(), hotelId);
        if(hotel.isActive()) {
            inventoryService.InitializeRoomForAYear(savedRoom);
        } else {
            log.warn("Hotel with ID: {} is not active. Inventory initialization skipped for room with ID: {}", hotelId, savedRoom.getId());
        }
        
        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Fetching hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", hotelId);
            return new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        List<Room> rooms = hotel.getRooms();
        return rooms.stream().map(room -> modelMapper.map(room, RoomDto.class)).toList();
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Fetching room with ID: {}", roomId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            log.error("Room not found with ID: {}", roomId);
            return new ResourceNotFoundException("Room not found with ID: " + roomId);
        });
        if(user.equals(room.getHotel().getOwner())){
            log.info("Successfully fetched room with ID: {}", roomId);
        } else {
            log.warn("Unauthorized access attempt to room with ID: {} by user with ID: {}", roomId, user.getId());
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }   
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Fetching room with ID: {}", roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            log.error("Room not found with ID: {}", roomId);
            return new ResourceNotFoundException("Room not found with ID: " + roomId);
        });
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(room.getHotel().getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        roomRepository.delete(room);
        inventoryService.deleteAllInventories(room);
        log.info("Successfully deleted room with ID: {} and its future inventories", roomId);
    }

    @Override
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto room) {
        log.info("Fetching room with ID: {}", roomId);
        Room existingRoom = roomRepository.findById(roomId).orElseThrow(() -> {
            log.error("Room not found with ID: {}", roomId);
            return new ResourceNotFoundException("Room not found with ID: " + roomId);
        });
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(existingRoom.getHotel().getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        modelMapper.map(room, existingRoom);
        existingRoom.setId(roomId);
        existingRoom = roomRepository.save(existingRoom);
        return modelMapper.map(existingRoom, RoomDto.class);
    }

    
}