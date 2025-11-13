package com.humanamente.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Please provide a valid email")
    String email,

    @NotBlank(message = "Password cannot be null")
    String password
) {
    
}
