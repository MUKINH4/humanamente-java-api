package com.humanamente.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.dto.LoginRequest;
import com.humanamente.api.dto.RegisterRequest;
import com.humanamente.api.dto.RegisterResponse;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;
import com.humanamente.api.model.enums.UserRoles;
import com.humanamente.api.service.AuthService;
import com.humanamente.api.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    private final MessageService messageService;

    public AuthController(AuthService authService, MessageService messageService) {
        this.authService = authService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest req) {
        User user = User.builder()
            .username(req.username())
            .email(req.email())
            .password(req.password())
            .role(req.role() != null ? req.role() : UserRoles.USER)
            .build();

        authService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse(messageService.getMessage("success.user.created")));
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody @Valid LoginRequest credentials) {
        return authService.login(credentials);
    }
}
