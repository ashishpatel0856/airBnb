package com.ashish.projects.VrboApp.service;


import com.ashish.projects.VrboApp.dto.ProfileUpdateRequestDto;
import com.ashish.projects.VrboApp.dto.UserDto;
import com.ashish.projects.VrboApp.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
