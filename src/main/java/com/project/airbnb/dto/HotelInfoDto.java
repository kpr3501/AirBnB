package com.project.airbnb.dto;

import java.util.List;

import lombok.Data;

@Data
public class HotelInfoDto {

    HotelDto hotel;
    List<RoomDto> rooms;

}
