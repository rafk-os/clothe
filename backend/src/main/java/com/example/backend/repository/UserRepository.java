package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query( value = "SELECT u from User u where u.username = ?1")
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u where u.email = ?1")
    Optional<User> findByEmail(String email);
}
