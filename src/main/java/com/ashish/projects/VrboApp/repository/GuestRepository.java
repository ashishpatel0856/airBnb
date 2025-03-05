package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
