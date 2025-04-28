package com.ashish.projects.airBnb.repository;

import com.ashish.projects.airBnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
