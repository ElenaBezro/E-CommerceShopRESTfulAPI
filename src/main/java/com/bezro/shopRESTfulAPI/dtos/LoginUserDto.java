package com.bezro.shopRESTfulAPI.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginUserDto {
    @NotBlank(message = "The username is required.")
    private String username;

    @NotBlank(message = "The password is required.")
    private String password;
}
