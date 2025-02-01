package com.demo.vulnerable.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.vulnerable.demo.model.User;
import com.demo.vulnerable.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    // Endpoint vulnérable à l'injection SQL
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        String sql = "SELECT * FROM user WHERE username LIKE '%" + query + "%'";
        return userRepository.findByCustomQuery(sql);
    }
    
    // Endpoint vulnérable au XSS
    @PostMapping("/comments")
    @ResponseBody
    public String addComment(@RequestBody String comment) {
        return "<div class='comment'>" + comment + "</div>";
    }
    
    // Endpoint avec IDOR
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // Pas de vérification d'autorisation
        return userRepository.findById(id).orElse(null);
    }
    
    // Mass Assignment vulnérable
    @PostMapping
    public User createUser(@RequestBody User user) {
        // Pas de validation des champs sensibles
        return userRepository.save(user);
    }
    
    // Exception Handler qui expose trop d'informations
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e) {
        return "Error: " + e.getMessage() + "\nStack trace: " + e.getStackTrace();
    }
}