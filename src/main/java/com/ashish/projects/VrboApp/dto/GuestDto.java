package com.ashish.projects.VrboApp.dto;

import com.ashish.projects.VrboApp.entity.enums.Gender;
import org.apache.catalina.User;

public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
