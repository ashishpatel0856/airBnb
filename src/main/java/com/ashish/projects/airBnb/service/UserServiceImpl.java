package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.exceptions.ResourceNotFoundException;
import com.ashish.projects.airBnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id"));
    }
}
