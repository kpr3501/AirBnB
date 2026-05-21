package com.project.airbnb.repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.airbnb.entity.Hotel;
import com.project.airbnb.entity.Inventory;
import com.project.airbnb.entity.Room;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);

    @Query("""
            select distinct i.hotel from Inventory i
            where i.city = :city
            and i.date between :checkInDate and :checkOutDate
            and (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            and i.closed = false
            group by i.hotel, i.room
            having count(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsByAvailableInventory(
        @Param("city") String city,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate,
        @Param("roomsCount") Integer roomsCount,
        @Param("dateCount") Long dateCount,
        Pageable pageable
    );

    @Query("""
            select i from Inventory i
            where i.room.id = :roomId
            and i.date between :checkInDate and :checkOutDate
            and (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            and i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
        @Param("roomId") Long roomId,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate,
        @Param("roomsCount") Integer roomsCount
    );
 @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("numberOfRooms") int numberOfRooms);

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.bookedCount = i.bookedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.reservedCount >= :numberOfRooms
                  AND i.closed = false
            """)
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") int numberOfRooms);

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    List<Inventory> findByRoomOrderByDate(Room room);

    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> getInventoryAndLockBeforeUpdate(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.surgeFactor = :surgeFactor,
                    i.closed = :closed
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
            """)
    void updateInventory(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("closed") boolean closed,
                         @Param("surgeFactor")BigDecimal surgeFactor);

    List<Inventory> findByRoomId(Long roomId);

    // @Query("""
    //    SELECT new RoomPriceDto(
    //         i.room,
    //         CASE
    //             WHEN COUNT(i) = :dateCount THEN AVG(i.price)
    //             ELSE NULL
    //         END
    //     )
    //    FROM Inventory i
    //    WHERE i.hotel.id = :hotelId
    //          AND i.date BETWEEN :startDate AND :endDate
    //          AND (i.totalCount - i.bookedCount) >= :roomsCount
    //          AND i.closed = false
    //    GROUP BY i.room
    //    """)
    // List<RoomPriceDto> findRoomAveragePrice(
    //         @Param("hotelId") Long hotelId,
    //         @Param("startDate") LocalDate startDate,
    //         @Param("endDate") LocalDate endDate,
    //         @Param("roomsCount") Long roomsCount,
    //         @Param("dateCount") Long dateCount
    // );

}
