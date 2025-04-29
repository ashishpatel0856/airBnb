package com.ashish.projects.airBnb.security;

import com.ashish.projects.airBnb.dto.LoginDto;
import com.ashish.projects.airBnb.dto.SignUpRequestDto;
import com.ashish.projects.airBnb.dto.UserDto;
import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // used for login signup and refresh
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {

         User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if (user != null) {
            throw new RuntimeException("User is already registered with same email");
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser=userRepository.save(newUser);
        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto) {
       Authentication authentication=
               authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                       loginDto.getEmail(), loginDto.getPassword()));

       User user=(User)authentication.getPrincipal();
       String[] arr =new String[2];
       arr[0]=jwtService.generateAccessToken(user);
       arr[1]=jwtService.generateRefreshToken(user);

       return arr;

    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElse(null);
        return jwtService.generateRefreshToken(user);
    }
}
