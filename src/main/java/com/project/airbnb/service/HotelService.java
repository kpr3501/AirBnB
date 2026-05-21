package com.project.airbnb.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.project.airbnb.dto.HotelDto;
import com.project.airbnb.dto.HotelInfoDto;


public interface HotelService {
    HotelDto createNewHotel(HotelDto hotel);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotel);
    void deleteHotelById(Long hotelId);
    void actiavateHotelById(Long hotelId);
    @Nullable
    HotelInfoDto getHotelInfoById(Long hotelId);
    List<HotelDto> getAllHotels();


}
