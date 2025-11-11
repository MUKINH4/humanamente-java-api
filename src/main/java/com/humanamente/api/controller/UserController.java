package com.humanamente.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.dto.RegisterResponse;
import com.humanamente.api.model.User;
import com.humanamente.api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
public class UserController {
    
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid User user) {
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterResponse("User created"));
    }

}
