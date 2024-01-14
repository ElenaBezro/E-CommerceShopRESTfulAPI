package com.bezro.shopRESTfulAPI.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationUserDto {
    @NotBlank(message = "The username is required.")
    @Size(min = 3, max = 20, message = "The username must be from 3 to 20 characters.")
    private String username;

    @NotBlank(message = "The password is required.")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$", message = "Password must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.")
    private String password;

    @NotBlank(message = "The confirm Password is required.")
    private String confirmPassword;

    @Email(message = "The email is not a valid email.")
    @NotEmpty(message = "The email is required.")
    private String email;
}
