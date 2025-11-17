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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.humanamente.api.model.User;
import com.humanamente.api.service.UserService;
import com.humanamente.api.dto.ApiResponse;
import com.humanamente.api.dto.UserEdit;
import com.humanamente.api.service.MessageService;
import com.humanamente.api.dto.ChangePasswordRequest;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.service.TokenService;
import com.humanamente.api.dto.UserUpdateResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final MessageService messageService;
    private final TokenService tokenService;

    public UserController(UserService userService, MessageService messageService, TokenService tokenService) {
        this.userService = userService;
        this.messageService = messageService;
        this.tokenService = tokenService;
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
    public ResponseEntity<ApiResponse<UserUpdateResponse>> update(
            @PathVariable String id, 
            @RequestBody @Valid UserEdit userEdit,
            @AuthenticationPrincipal User authenticatedUser) {
        
        // Guarda o email antigo antes de atualizar
        User oldUser = userService.findById(id);
        String oldEmail = oldUser.getEmail();
        
        // Atualiza o usuário
        User updated = userService.update(id, userEdit);
        
        // Verifica se o email foi alterado
        boolean emailChanged = !oldEmail.equals(updated.getEmail());
        
        // Gera novo token apenas se o email foi alterado
        TokenDTO newToken = emailChanged ? tokenService.createToken(updated) : null;
        
        UserUpdateResponse response = new UserUpdateResponse(updated, newToken);
        
        String message = emailChanged 
            ? messageService.getMessage("success.user.updated.with.token")
            : messageService.getMessage("success.user.updated");
        
        return ResponseEntity.ok(new ApiResponse<>(message, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.user.deleted"), null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<TokenDTO>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal User authenticatedUser) {
        
        userService.changePassword(authenticatedUser.getId(), request);
        
        // Gera novo token após mudança de senha
        TokenDTO newToken = tokenService.createToken(authenticatedUser);
        
        return ResponseEntity.ok(new ApiResponse<>(
            messageService.getMessage("success.password.changed"), 
            newToken
        ));
    }
}
