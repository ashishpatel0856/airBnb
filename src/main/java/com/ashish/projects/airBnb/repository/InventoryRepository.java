package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.Inventory;
import com.ashish.projects.airBnb.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByDateAfterAndRoom(LocalDate date, Room room);
    boolean existsByRoom(Room room);

    boolean existsByRoomAndDate(Room room, LocalDate date);


    @Query("""
   SELECT DISTINCT i.hotel
   FROM Inventory i
   WHERE i.city = :city
   AND i.date BETWEEN :startDate AND :endDate
   AND i.closed = false
   AND (i.totalCount - i.bookedCount) >= :roomsCount
   GROUP BY i.hotel, i.room
   HAVING COUNT(i.date) = :dateCount
""")
    Page<Hotel> findHotelswithAvailableRoom(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    @Query("""
           SELECT i
           FROM Inventory i
           WHERE i.room.id = :roomId
              AND i.date BETWEEN :startDate AND :endDate
   AND i.closed = false
   AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
           
""")
@Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

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
                        @Param("numberOfRooms") int numberOfRooms
                        );





    @Modifying
    @Query("""
                   UPDATE Inventory i
SET i.bookedCount = i.bookedCount + :numberOfRooms
WHERE i.room.id = :roomId
AND i.date BETWEEN :startDate AND :endDate
AND (i.totalCount - i.bookedCount) >= :numberOfRooms
AND i.reservedCount >= :numberOfRooms
AND i.closed = false
""")
    void cancelBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms
    );



    @Modifying
    @Query("""
                   UPDATE Inventory i
SET i.bookedCount = i.bookedCount + :numberOfRooms
WHERE i.room.id = :roomId
AND i.date BETWEEN :startDate AND :endDate
AND (i.totalCount - i.bookedCount) >= :numberOfRooms
AND i.closed = false
""")
    void initBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") int numberOfRooms
    );


    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

   List<Inventory> findByRoomOrderByDate(Room room);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                   AND i.date BETWEEN : startDate AND :endDate
""")
    void getInventoryAndLockBeforeUpdate(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate


    );




    @Modifying
    @Query("""
                   UPDATE Inventory i
SET i.surgeFactor = :surgeFactor , i.closed = :closed
WHERE i.room.id = :roomId
AND i.date BETWEEN :startDate AND :endDate


""")
    void updateInventory(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("closed") boolean closed,
                         @Param("surgeFactor")BigDecimal surgeFactor

                         );

}
