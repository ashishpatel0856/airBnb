package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
