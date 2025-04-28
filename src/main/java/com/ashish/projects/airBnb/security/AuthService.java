package com.ashish.projects.airBnb.security;

import com.ashish.projects.airBnb.dto.SignUpRequestDto;
import com.ashish.projects.airBnb.dto.UserDto;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // used for login signup and refresh
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = userRepository.findByEmail(signUpRequestDto.getEmail().orElsethro)
    }
}
