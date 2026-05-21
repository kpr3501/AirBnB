package com.project.airbnb.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.airbnb.dto.HotelDto;
import com.project.airbnb.dto.HotelInfoDto;
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
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    
    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating new hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);
        hotel = hotelRepository.save(hotel);
        log.info("Hotel created with ID: {}", hotel.getId());  
        HotelDto createdHotelDto = modelMapper.map(hotel, HotelDto.class); 
        return createdHotelDto;
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", id);
            return new ResourceNotFoundException("Hotel not found with ID: " + id);
        });
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", id);
            return new ResourceNotFoundException("Hotel not found with ID: " + id);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        log.info("Updating hotel with ID: {}", id);
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        log.info("Hotel updated with ID: {}", id);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long hotelId) {
        log.info("Fetching hotel with ID: {}", hotelId);
        
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", hotelId);
            return new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        // Fetch all rooms for this hotel first
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        
        // Delete all inventories for all rooms
        for (Room room : rooms) {
            log.info("Deleting inventories for room with ID: {} in hotel with ID: {}", room.getId(), hotelId);
            inventoryService.deleteAllInventories(room);
        }
        
        // Delete all rooms associated with the hotel
        log.info("Deleting all rooms for hotel with ID: {}", hotelId);
        roomRepository.deleteByHotelId(hotelId);
        
        // Delete the hotel
        log.info("Deleting hotel with ID: {}", hotelId);
        hotelRepository.deleteById(hotelId);
        log.info("Hotel deleted with ID: {}", hotelId);
    }

    @Override
    @Transactional
    public void actiavateHotelById(Long hotelId) {
        log.info("Fetching hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", hotelId);
            return new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        log.info("Activating hotel with ID: {}", hotelId);
        hotel.setActive(true);
        hotelRepository.save(hotel);
        
        // Fetch rooms explicitly to avoid lazy loading issues
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        for (Room room : rooms) {
            log.info("Initializing inventory for room with ID: {} in hotel with ID: {}", room.getId(), hotelId);
            inventoryService.InitializeRoomForAYear(room);
        }
        log.info("Hotel activated with ID: {}", hotelId);
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        // TODO Auto-generated method stub
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> {
            log.error("Hotel not found with ID: {}", hotelId);
            return new ResourceNotFoundException("Hotel not found with ID: " + hotelId);
        });
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        HotelInfoDto hotelInfo = new HotelInfoDto();
        hotelInfo.setHotel(modelMapper.map(hotel, HotelDto.class));
        hotelInfo.setRooms(rooms.stream().map(room -> modelMapper.map(room, RoomDto.class)).toList());
        return hotelInfo;
    }

    @Override
    public List<HotelDto> getAllHotels() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return hotelRepository.findByOwner(user).stream().map(hotel -> modelMapper.map(hotel, HotelDto.class)).toList();
    }


}


