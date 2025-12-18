package com.ashish.projects.airBnb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank
    @Email
    private String email;

    @Size(min = 8)
    private String password;

}
