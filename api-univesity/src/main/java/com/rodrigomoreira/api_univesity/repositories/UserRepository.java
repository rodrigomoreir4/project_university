package com.rodrigomoreira.api_univesity.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigomoreira.api_univesity.domain.users.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(Long id);
    
}
