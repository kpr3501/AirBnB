package com.project.airbnb.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HotelPriceDto {

    private Long id;
    private String name;
    private String city;
    private BigDecimal price;

    public HotelPriceDto(Long id, String name, String city, Double price) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.price = price != null ? BigDecimal.valueOf(price) : null;
    }

}
