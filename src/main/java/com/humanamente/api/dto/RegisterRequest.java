package com.humanamente.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.humanamente.api.model.enums.UserRoles;

public record RegisterRequest(
    @NotBlank(message = "{NotBlank.user.username}")
    String username,
    @NotBlank(message = "{NotBlank.user.email}")
    @Email(message = "{Email.user.email}")
    @Column(unique = true)
    String email,
    @NotBlank(message = "{NotBlank.user.password}")
    String password,
    @NotBlank(message = "{NotBlank.user.confirmPassword}")
    String confirmPassword,

    @Enumerated(EnumType.STRING)
    UserRoles role
) {
    @AssertTrue(message = "{Validation.password.mismatch}")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }
}
