package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Inventory;
import com.ashish.projects.airBnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByDateAfterAndRoom(LocalDate date, Room room);
}
