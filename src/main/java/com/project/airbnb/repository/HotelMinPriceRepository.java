package com.project.airbnb.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.airbnb.dto.HotelPriceDto;
import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.HotelMinPrice;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
            select new com.project.airbnb.dto.HotelPriceDto(i.hotel.id, i.hotel.name, i.hotel.city, AVG(i.price))
            from HotelMinPrice i
            where i.hotel.city = :city
            and i.date between :checkInDate and :checkOutDate
            and i.hotel.active = true
            group by i.hotel.id, i.hotel.name, i.hotel.city
            """)
    Page<HotelPriceDto> findHotelsByAvailableInventory(
        @Param("city") String city,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate,
        @Param("roomsCount") Integer roomsCount,
        @Param("dateCount") Long dateCount,
        Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
