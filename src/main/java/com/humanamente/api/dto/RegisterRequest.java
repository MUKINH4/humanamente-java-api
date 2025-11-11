package com.humanamente.api.dto;

public record RegisterRequest(
    String email,
    String password,
    String confirmPassword
) {
    
}
