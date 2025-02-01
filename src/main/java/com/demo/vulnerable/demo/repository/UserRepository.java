package com.demo.vulnerable.demo.repository;

import com.demo.vulnerable.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Méthode vulnérable à l'injection SQL
    @Query(value = "SELECT * FROM user WHERE username LIKE %:username%", nativeQuery = true)
    List<User> searchByUsername(@Param("username") String username);
}