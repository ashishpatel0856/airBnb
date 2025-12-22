package com.ashish.projects.airBnb.dto;

import com.ashish.projects.airBnb.entity.enums.Gender;
import com.ashish.projects.airBnb.entity.enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class SignUpRequestDto {
    private Long id;
    private String name;
    private String email;
    private String password;

    private Gender gender;
    private LocalDate dateOfBirth;

    private Set<Role> roles;


}
