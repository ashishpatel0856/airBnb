package com.ashish.projects.airBnb.dto;

import com.ashish.projects.airBnb.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDto {
    private Long id;
    private Long userId;
    private String userName;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;

}

