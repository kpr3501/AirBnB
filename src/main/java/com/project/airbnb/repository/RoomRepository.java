package com.project.airbnb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.airbnb.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByHotelId(Long hotelId);
    
    void deleteByHotelId(Long hotelId);
}
