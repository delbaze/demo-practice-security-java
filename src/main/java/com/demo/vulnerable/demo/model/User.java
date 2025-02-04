package com.demo.vulnerable.demo.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.ColumnTransformer;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String username;
    
    @NotBlank
    @Size(min = 60, max = 60)  // Taille pour BCrypt hash
    private String password;    // Sera hashé avant stockage
    
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    
    // Chiffrement des données sensibles
    @ColumnTransformer(
        read = "AES_DECRYPT(credit_card, 'key')",
        write = "AES_ENCRYPT(?, 'key')"
    )
    private String creditCard;
    
    @Column(nullable = false)
    private boolean isAdmin = false;  // Valeur par défaut à false
    
    // Constructeur par défaut
    public User() {}
    
    // Constructeur sécurisé avec validation
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.isAdmin = false;  // Toujours false par défaut
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}