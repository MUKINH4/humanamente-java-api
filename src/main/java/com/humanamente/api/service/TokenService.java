package com.humanamente.api.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenService {

    @Value("${jwt.secret:minha-chave-secreta-super-segura-123456}")
    private String secret;

    @Value("${jwt.expiration:3600}")
    private Long expirationTime;

    public TokenDTO createToken(User user) {
        Instant expirationAt = LocalDateTime.now().plusMinutes(60).toInstant(ZoneOffset.ofHours(-3));
        
        String token = JWT.create()
            .withIssuer("Humanamente API")
            .withSubject(user.getEmail())
            .withClaim("id", user.getId())
            .withClaim("role", user.getRole().name())
            .withIssuedAt(Instant.now())
            .withExpiresAt(expirationAt)
            .sign(Algorithm.HMAC256(secret));
        
        log.info("Novo token gerado para usuário: {} com expiração em: {}", user.getEmail(), expirationAt);
        
        return new TokenDTO(token, user.getUsername());
    }

    public String validateToken(String token) {
        try {
            var verifiedToken = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("Humanamente API")
                .build()
                .verify(token);
            
            String email = verifiedToken.getSubject();
            log.debug("Token válido para usuário: {}", email);
            
            return email;
            
        } catch (JWTVerificationException e) {
            log.error("Token inválido: {}", e.getMessage());
            return null;
        }
    }

    public User getUserFromToken(String token) {
        try {
            var verifiedToken = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("Humanamente API")
                .build()
                .verify(token);

            return User.builder()
                .id(verifiedToken.getClaim("id").asString())
                .email(verifiedToken.getSubject())
                .build();
                
        } catch (JWTVerificationException e) {
            log.error("Erro ao extrair usuário do token: {}", e.getMessage());
            return null;
        }
    }
}
