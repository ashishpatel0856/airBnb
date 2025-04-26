package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
