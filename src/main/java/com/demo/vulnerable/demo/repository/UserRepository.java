package com.demo.vulnerable.demo.repository;

import com.demo.vulnerable.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Méthode sécurisée utilisant JPQL au lieu de SQL natif
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

    // Méthode pour vérifier les doublons de username
    boolean existsByUsername(String username);

    // Méthode pour vérifier les doublons d'email
    boolean existsByEmail(String email);
}