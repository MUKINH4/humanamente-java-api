package com.humanamente.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humanamente.api.model.User;
import com.humanamente.api.service.UserService;
import com.humanamente.api.dto.ApiResponse;
import com.humanamente.api.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final MessageService messageService;

    public UserController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody @Valid User user) {
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(messageService.getMessage("success.user.created"), created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody @Valid User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.user.deleted"), null));
    }
}
