package com.demo.vulnerable.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Endpoint vulnérable à l'injection SQL (utilisant JdbcTemplate)
    @GetMapping("/search")
    public List<Map<String, Object>> searchUsers(@RequestParam String query) {
        String sql = "SELECT * FROM user WHERE username LIKE '%" + query + "%'";
        return jdbcTemplate.queryForList(sql);
    }

 
    @PostMapping(path = "/comments", consumes = "application/x-www-form-urlencoded")
    public String addComment(@RequestParam String comment) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
            .append("<html>\n")
            .append("<head>\n")
            .append("    <title>Commentaire ajouté</title>\n")
            .append("</head>\n")
            .append("<body>\n")
            .append("    <h1>Votre commentaire :</h1>\n")
            .append("    <div class='comment'>").append(comment).append("</div>\n\n")
            .append("    <h2>Ajouter un autre commentaire :</h2>\n")
            .append("    <form method='post' action='/comments'>\n")
            .append("        <textarea name='comment'></textarea>\n")
            .append("        <button type='submit'>Envoyer</button>\n")
            .append("    </form>\n")
            .append("</body>\n")
            .append("</html>");

        return html.toString();
    }
    // @PostMapping("/comments")
    // @ResponseBody
    // public String addComment(@RequestBody String comment) {
    //     // Échapper les caractères spéciaux HTML
    //     String escapedComment = HtmlUtils.htmlEscape(comment);
    //     return "<div class='comment'>" + escapedComment + "</div>";
    // }

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
