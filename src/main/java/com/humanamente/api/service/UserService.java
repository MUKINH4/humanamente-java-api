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

import com.humanamente.api.model.User;
import com.humanamente.api.repository.UserRepository;

@Service
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
        return repository.findByUsername(usernameOrEmail)
            .or(() -> repository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new UsernameNotFoundException(messageService.getMessage("error.user.not.found.username.or.email", usernameOrEmail)));
    }

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
    public User update(String id, User user) {
        User existing = findById(id);
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repository.save(existing);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(String id) {
        repository.deleteById(id);
    }
}
