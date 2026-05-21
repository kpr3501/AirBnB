package com.project.airbnb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import com.project.airbnb.entity.enums.BookingStatus;
import lombok.Data;

@Data
public class BookingDto {

    private long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus status;
    private Set<GuestDto> guests;
    private BigDecimal amount;

}
