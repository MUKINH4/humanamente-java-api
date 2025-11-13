package com.humanamente.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.humanamente.api.model.enums.UserRoles;

public record RegisterRequest(
    @NotBlank(message = "Username cannot be null")
    String username,
    @NotBlank(message = "Email cannot be null")
    @Email(message = "Please provide a valid email")
    @Column(unique = true)
    String email,
    @NotBlank(message = "Password cannot be null")
    String password,
    @NotBlank(message = "Confirm Password cannot be null")
    String confirmPassword,

    @Enumerated(EnumType.STRING)
    UserRoles role
) {
    @AssertTrue(message = "Password do not match")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }
}
