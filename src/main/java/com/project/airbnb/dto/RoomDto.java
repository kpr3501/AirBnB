package com.project.airbnb.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RoomDto {

    private long id;
    private String type;
    private BigDecimal basePrice;

    private String[] photos;

    private String[] amenities;

    private Integer totalCount;
 
    private Integer capacity;

}
