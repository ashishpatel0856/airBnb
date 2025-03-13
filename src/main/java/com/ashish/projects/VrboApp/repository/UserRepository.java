package com.ashish.projects.VrboApp.repository;

import com.ashish.projects.VrboApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
