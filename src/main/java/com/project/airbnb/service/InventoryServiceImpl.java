package com.project.airbnb.service;

import com.project.airbnb.repository.RoomRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.modelmapper.ModelMapper;
// import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.airbnb.dto.HotelPriceDto;
import com.project.airbnb.dto.HotelSearchRequest;
import com.project.airbnb.dto.InventoryDto;
import com.project.airbnb.dto.UpdateInventoryRequestDto;
import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.Inventory;
import com.project.airbnb.entity.Room;
import com.project.airbnb.entity.User;
import com.project.airbnb.exception.UnauthorizedException;
import com.project.airbnb.repository.HotelMinPriceRepository;
import com.project.airbnb.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    // private final ModelMapper modelMapper;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    @Override
    @Transactional
    public void InitializeRoomForAYear(Room roomDto) {
        log.info("Initializing inventory for room with ID: {}", roomDto.getId());
        // Logic to initialize inventory for the room for a year
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        
        Hotel hotel = roomDto.getHotel();
        String city = hotel != null ? hotel.getCity() : null;
        
        while(!today.isAfter(endDate)) {
            Inventory inventory = Inventory.builder()
            .hotel(hotel)
            .room(roomDto)
            .date(today)
            .bookedCount(0)
            .reservedCount(0)
            .totalCount(roomDto.getTotalCount())
            .surgeFactor(BigDecimal.ONE)
            .price(roomDto.getBasePrice())
            .city(city)
            .closed(false)
            .build();
            inventoryRepository.save(inventory);
            today = today.plusDays(1);
        }
        log.info("Successfully initialized inventory for room with ID: {}", roomDto.getId());            
        
    }
    @Override
    @Transactional
    public void deleteAllInventories(Room room) {
        // TODO Auto-generated method stub
        log.info("Deleting future inventories for room with ID: {}", room.getId());
        inventoryRepository.deleteByRoom(room);
        log.info("Successfully deleted future inventories for room with ID: {}", room.getId());
    }
    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        Long dateCount = ChronoUnit.DAYS.between(searchRequest.getStartDate(), searchRequest.getEndDate()) + 1;
        
        Page<HotelPriceDto> hotels = hotelMinPriceRepository.findHotelsByAvailableInventory(
            searchRequest.getCity(),
            searchRequest.getStartDate(),
            searchRequest.getEndDate(),
            searchRequest.getRoomsCount(),
            dateCount,
            pageable);
        // ).map((hotel) -> modelMapper.map(hotel, HotelPriceDto.class));

        return hotels;

    }
    @Override
    public List<InventoryDto> getInventoryForRoom(Long roomId) {
       Room room = roomRepository.findById(roomId).orElseThrow();
        // Convert room to InventoryDto (assuming you have a method for this)
        // return room.getInventory().stream().map(this::convertToInventoryDto).collect(Collectors.toList());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(room.getHotel().getOwner())){
            log.info("Successfully fetched inventory for room with ID: {}", roomId);
        } else {
            log.warn("Unauthorized access attempt to inventory for room with ID: {} by user with ID: {}", roomId, user.getId());
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        return inventoryRepository.findByRoomOrderByDate(room).stream().map((element)->modelMapper.map(element, InventoryDto.class)).toList();

    }
    @Override
    @Transactional
    public void updateInventoryForRoom(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        // TODO Auto-generated method stub
        Room room = roomRepository.findById(roomId).orElseThrow();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.equals(room.getHotel().getOwner())){
            log.info("Successfully authorized inventory update for room with ID: {}", roomId);
        } else {
            log.warn("Unauthorized access attempt to update inventory for room with ID: {} by user with ID: {}", roomId, user.getId());
            throw new UnauthorizedException("Hotel does not belongs to current user");
        }
        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());  
        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getClosed(), updateInventoryRequestDto.getSurgeFactor());
        log.info("Successfully updated inventory for room with ID: {}", roomId);
        throw new UnsupportedOperationException("Unimplemented method 'updateInventoryForRoom'");
    }
}
