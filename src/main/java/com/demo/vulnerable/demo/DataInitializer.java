package com.demo.vulnerable.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.demo.vulnerable.demo.model.User;
import com.demo.vulnerable.demo.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123"); // Mot de passe en clair
        admin.setEmail("admin@company.com");
        admin.setCreditCard("4532-7165-9087-2342");
        admin.setAdmin(true);
        userRepository.save(admin);

        // Regular user
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("password123");
        user.setEmail("john@example.com");
        user.setCreditCard("4539-5789-3456-2190");
        user.setAdmin(false);
        userRepository.save(user);
    }
}