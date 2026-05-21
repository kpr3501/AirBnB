package com.project.airbnb.service;

import java.util.List;

import com.project.airbnb.dto.RoomDto;


public interface RoomService {

    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
    RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto room);
}
