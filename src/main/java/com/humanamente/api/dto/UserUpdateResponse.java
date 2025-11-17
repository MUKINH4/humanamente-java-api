package com.humanamente.api.dto;

import com.humanamente.api.model.User;

public record UserUpdateResponse(
    User user,
    TokenDTO token  // Novo token se o email foi alterado
) {}
