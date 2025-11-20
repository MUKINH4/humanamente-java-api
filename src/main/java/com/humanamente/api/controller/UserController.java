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
        try {
            User created = userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.getMessage("success.user.created"), created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(messageService.getMessage("error.user.not.found"), null));
            }

            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.user.found"), user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> findAll() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.users.found"), users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> findByEmail(@PathVariable String email) {
        try {
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.user.found"), user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> update(
            @PathVariable String id, 
            @RequestBody @Valid UserEdit userEdit,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            User oldUser = userService.findById(id);

            if (oldUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(messageService.getMessage("error.user.not.found"), null));
            }

            String oldEmail = oldUser.getEmail();

            if (!authenticatedUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(messageService.getMessage("error.user.unauthorized"), null));
            }
            
            User updated = userService.update(id, userEdit);
            
            boolean emailChanged = !oldEmail.equals(updated.getEmail());
            
            TokenDTO newToken = emailChanged ? tokenService.createToken(updated) : null;
            
            UserUpdateResponse response = new UserUpdateResponse(updated, newToken);
            
            String message = emailChanged 
                ? messageService.getMessage("success.user.updated.with.token")
                : messageService.getMessage("success.user.updated");
            
            return ResponseEntity.ok(new ApiResponse<>(message, response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(messageService.getMessage("success.user.deleted"), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<TokenDTO>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            userService.changePassword(authenticatedUser.getId(), request);
            
            TokenDTO newToken = tokenService.createToken(authenticatedUser);
            
            return ResponseEntity.ok(new ApiResponse<>(
                messageService.getMessage("success.password.changed"), 
                newToken
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(messageService.getMessage("error.internal"), null));
        }
    }
}
