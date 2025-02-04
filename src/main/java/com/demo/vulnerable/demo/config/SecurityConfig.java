package com.demo.vulnerable.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Pour les besoins de la démo
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/api/users/**").permitAll()  // Pour la démo
            .anyRequest().authenticated()
            .and()
            .headers()
            .frameOptions().sameOrigin();  // Pour H2 Console
        
        return http.build();
    }
}