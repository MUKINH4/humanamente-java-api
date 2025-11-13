package com.humanamente.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.humanamente.api.dto.LoginRequest;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;
import com.humanamente.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public TokenDTO login(LoginRequest credentials) {
        try {
            var authentication = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());
            User user = (User) authManager.authenticate(authentication).getPrincipal();
            return tokenService.createToken(user);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or invalid credentials");
        }
    }
}
