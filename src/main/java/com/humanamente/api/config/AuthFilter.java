package com.humanamente.api.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.humanamente.api.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // verificar o HEADER
            var header = request.getHeader("Authorization");
            if (header == null) {
                filterChain.doFilter(request, response);
                return;
            }

        // HEADER é do tipo Bearer
        if (!header.startsWith("Bearer ")) {
            response.setStatus(401);
            return;
        }
        // validar o Token
        var token = header.replace("Bearer ", "");
        var user = tokenService.getUserFromToken(token);
        // autentica o usuário

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
    
}