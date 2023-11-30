package com.mountblue.blogapplication.restControllers;

import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
public class SecurityRestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ResponseEntity<String> showLoginPage() {
        return ResponseEntity.ok("loginPage");
    }

    @GetMapping("/access-denied")
    public ResponseEntity<String> showAccessDenied() {
        return ResponseEntity.ok("access-denied");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String name,
                                               @RequestParam String email,
                                               @RequestParam String password) {
        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("User with email " + email + " already exists.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("{noop}" + password);
        user.setRole("author");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}

