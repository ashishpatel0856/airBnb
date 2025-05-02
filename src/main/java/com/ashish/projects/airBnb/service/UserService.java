package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.dto.ProfileUpdateRequestDto;
import com.ashish.projects.airBnb.entity.User;

public interface UserService {

    User getUserById(Long userId);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);
}
