package com.ashish.projects.airBnb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDto {
    private String name;
    @NotBlank
    @Email
    private String email;

    @Size(min = 8)
    private String password;

    private Long id;
}
