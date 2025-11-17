package com.humanamente.api.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.humanamente.api.dto.LoginRequest;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    @CacheEvict(value = "users", allEntries = true)
    public User registerUser(User user) {
        // Remove a criptografia duplicada - UserService.create já faz isso
        return userService.create(user);
    }

    public TokenDTO login(LoginRequest credentials) {
        try {
            log.info("Tentativa de login para: {}", credentials.email());
            
            var authentication = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());
            User user = (User) authManager.authenticate(authentication).getPrincipal();
            
            log.info("Login bem-sucedido para: {}", user.getEmail());
            
            // Sempre gera um novo token a cada login
            TokenDTO token = tokenService.createToken(user);
            
            return token;
            
        } catch (AuthenticationException ex) {
            log.error("Falha no login para: {} - Erro: {}", credentials.email(), ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }
    }
}
