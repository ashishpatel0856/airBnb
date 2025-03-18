package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.Guest;
import com.ashish.projects.VrboApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}
