package com.humanamente.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.humanamente.api.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
