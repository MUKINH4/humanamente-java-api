package com.humanamente.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "{error.current.password.required}")
    String currentPassword,
    
    @NotBlank(message = "{error.new.password.required}")
    @Size(min = 6, message = "{error.new.password.size}")
    String newPassword
) {}
