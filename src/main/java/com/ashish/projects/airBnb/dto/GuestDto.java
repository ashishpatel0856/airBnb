package com.ashish.projects.airBnb.dto;

import com.ashish.projects.airBnb.entity.User;
import com.ashish.projects.airBnb.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private Long userId;
    private String userName;
    private String name;
    private Gender gender;
    private Integer age;
}

