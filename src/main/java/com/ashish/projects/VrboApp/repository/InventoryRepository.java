package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.Inventory;
import com.ashish.projects.VrboApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByDateAfterAndRoom(LocalDate date, Room room);
}
