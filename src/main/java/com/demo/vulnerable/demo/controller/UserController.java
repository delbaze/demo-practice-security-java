package com.demo.vulnerable.demo.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.demo.vulnerable.demo.model.User;
import com.demo.vulnerable.demo.repository.UserRepository;
import com.demo.vulnerable.demo.dto.UserDto;
import com.demo.vulnerable.demo.dto.CreateUserDto;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Correction Injection SQL - Utilisation de JPA
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<UserDto> users = userRepository.findByUsernameContaining(query)
            .stream()
            .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // Correction XSS - Encodage HTML et validation des entrées
    @PostMapping(path = "/comments", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> addComment(@RequestParam String comment) {
        if (comment == null || comment.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Encodage HTML pour prévenir XSS
        String encodedComment = Encode.forHtml(comment);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <title>Commentaire ajouté</title>\n")
            .append("    <meta http-equiv='Content-Security-Policy' ")
            .append("          content=\"default-src 'self'; script-src 'self'; style-src 'self';\">\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <h1>Votre commentaire :</h1>\n")
            .append("    <div class='comment'>").append(encodedComment).append("</div>\n\n")
            .append("    <h2>Ajouter un autre commentaire :</h2>\n")
            .append("    <form method='post' action='/api/users/comments'>\n")
            .append("        <textarea name='comment' maxlength='1000' required></textarea>\n")
            .append("        <button type='submit'>Envoyer</button>\n")
            .append("    </form>\n")
            .append("</body>\n")
            .append("</html>");

        return ResponseEntity.ok()
            .header("X-XSS-Protection", "1; mode=block")
            .header("X-Content-Type-Options", "nosniff")
            .body(html.toString());
    }

    // Correction IDOR - Vérification d'autorisation
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérification que l'utilisateur accède à son propre profil ou est admin
        if (!user.getUsername().equals(currentUsername) && !auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();        }

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }

    // Correction Mass Assignment - Utilisation de DTO et validation
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        // Vérification des doublons
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        // Création sécurisée de l'utilisateur
        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setEmail(createUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setAdmin(false); // Valeur par défaut forcée

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()));
    }

    // Gestion sécurisée des exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception e) {
        // Log l'erreur complète pour le debugging
        logger.error("Une erreur est survenue", e);
        
        // Retourne un message générique à l'utilisateur
        return ResponseEntity.internalServerError()
            .body("Une erreur est survenue. Veuillez réessayer plus tard.");
    }
}