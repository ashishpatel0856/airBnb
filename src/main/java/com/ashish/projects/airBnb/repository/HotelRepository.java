package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.Hotel;
import com.ashish.projects.airBnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel,Long>{

    List<Hotel> findByOwner(User user);
}
