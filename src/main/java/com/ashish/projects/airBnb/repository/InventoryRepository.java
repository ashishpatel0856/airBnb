package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
