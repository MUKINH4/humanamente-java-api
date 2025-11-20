package com.humanamente.api.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        try {
            if (user == null) {
                log.warn("Tentativa de registro com usuário nulo");
                throw new IllegalArgumentException("Usuário não pode ser nulo");
            }
            
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                log.warn("Tentativa de registro com email vazio");
                throw new IllegalArgumentException("Email é obrigatório");
            }
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                log.warn("Tentativa de registro com senha vazia para: {}", user.getEmail());
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            
            log.info("Iniciando registro para: {}", user.getEmail());
            User registered = userService.create(user);
            log.info("Registro bem-sucedido para: {}", user.getEmail());
            
            return registered;
        } catch (IllegalArgumentException e) {
            log.error("Validação falhou no registro: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado durante registro: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao registrar usuário");
        }
    }

    public TokenDTO login(LoginRequest credentials) {
        if (credentials == null) {
            log.warn("Tentativa de login com credenciais nulas");
            throw new IllegalArgumentException("Credenciais não podem ser nulas");
        }
        
        if (credentials.email() == null || credentials.email().isEmpty()) {
            log.warn("Tentativa de login com email vazio");
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (credentials.password() == null || credentials.password().isEmpty()) {
            log.warn("Tentativa de login com senha vazia para: {}", credentials.email());
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        log.info("Tentativa de login para: {}", credentials.email());
        
        var authentication = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());
        User user = (User) authManager.authenticate(authentication).getPrincipal();
        
        log.info("Login bem-sucedido para: {}", user.getEmail());
        
        TokenDTO token = tokenService.createToken(user);
        
        return token;

    }
}
