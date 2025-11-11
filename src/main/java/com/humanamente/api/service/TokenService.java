package com.humanamente.api.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.humanamente.api.dto.TokenDTO;
import com.humanamente.api.model.User;
import com.humanamente.api.model.enums.UserRoles;

@Service
public class TokenService {

    Instant expiresAt = LocalDateTime.now().plusMinutes(60).toInstant(ZoneOffset.ofHours(-3));

    Algorithm algorithm = Algorithm.HMAC256("secret");

    public TokenDTO createToken(User user) {
        var jwt = JWT.create()
            .withSubject(user.getId())
            .withClaim("role", user.getRole().toString())
            .withExpiresAt(expiresAt)
            .sign(algorithm);

        return new TokenDTO(jwt, user.getUsername());
    }

    public User getUserFromToken(String token) {
        var verifiedToken = JWT.require(algorithm).build().verify(token);

        return User.builder()
            .id(verifiedToken.getSubject())
            .role(UserRoles.valueOf(verifiedToken.getClaim("role").asString()))
            .build();

    }
     
}
