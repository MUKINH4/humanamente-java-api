package com.humanamente.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @NotBlank(message = "{NotBlank.user.email}")
    @Email(message = "{Email.user.email}")
    String email,

    @NotBlank(message = "{NotBlank.user.password}")
    String password
) {
    
}
