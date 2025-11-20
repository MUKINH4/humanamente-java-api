package com.humanamente.api.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.humanamente.api.dto.ChangePasswordRequest;
import com.humanamente.api.dto.UserEdit;
import com.humanamente.api.model.User;
import com.humanamente.api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    public UserService(UserRepository repository, @Lazy PasswordEncoder passwordEncoder, MessageService messageService){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = messageService;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.info("Carregando usuário: {}", usernameOrEmail);
        return repository.findByUsername(usernameOrEmail)
            .or(() -> repository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Cacheable(value = "users", key = "#id")
    public User findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.user.not.found", id)));
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "users", key = "'email_' + #email")
    public User findByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.user.email.not.found", email)));
    }

    @CacheEvict(value = "users", allEntries = true)
    public User update(String id, UserEdit userEdit) {
        User existing = findById(id);
        
        // Verifica se o email foi alterado
        boolean emailChanged = !existing.getEmail().equals(userEdit.email());
        
        existing.setUsername(userEdit.username());
        existing.setEmail(userEdit.email());
        
        User updated = repository.save(existing);
        
        if (emailChanged) {
            log.info("Email alterado para usuário: {} - Novo token será gerado", updated.getId());
        }
        
        return updated;
    }
    
    public boolean wasEmailChanged(User oldUser, User newUser) {
        return !oldUser.getEmail().equals(newUser.getEmail());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = findById(userId);
        
        // Verifica se a senha atual está correta
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException(messageService.getMessage("error.password.incorrect"));
        }
        
        // Atualiza para a nova senha
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repository.save(user);
        
        log.info("Senha alterada para usuário: {}", user.getEmail());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(String id) {
        repository.deleteById(id);
    }

    public void validateEmailUniqueness(
            String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException(messageService.getMessage("error.user.email.exists", email));
        }
    }
}
