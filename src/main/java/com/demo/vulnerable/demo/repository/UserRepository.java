package com.demo.vulnerable.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.vulnerable.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Méthode vulnérable à l'injection SQL
    @Query(value = "SELECT * FROM user WHERE username = ?1", nativeQuery = true)
    List<User> findByUsernameUnsafe(String username);
    
    // Méthode pour exécuter des requêtes SQL arbitraires (dangereuse)
    @Query(nativeQuery = true)
    List<User> findByCustomQuery(String sql);
}