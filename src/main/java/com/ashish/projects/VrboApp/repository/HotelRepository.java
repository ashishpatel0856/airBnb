package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.Hotel;
import com.ashish.projects.VrboApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);

}
