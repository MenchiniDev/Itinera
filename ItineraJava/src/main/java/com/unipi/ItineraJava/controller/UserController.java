package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.repository.UserRepository;
import com.unipi.ItineraJava.service.UserService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.DTO.SignupRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // http://localhost:8080/users/signup WORKING
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        System.out.println(signupRequest.getUsername());
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole("USER");
        user.setCreated(LocalDateTime.now().toString());
        user.setActive(true);
        user.setReported(false);
        System.out.println(user.getUsername());
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // http://localhost:8080/users/signup/admin WORKING
    @PostMapping("/signup/admin")
    public ResponseEntity<String> signupAdmin(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole("ADMIN");
        user.setCreated(LocalDateTime.now().toString());
        user.setActive(true);
        user.setReported(false);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // http://localhost:8080/users/login WORKING
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username");
        }
        passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword());
        String token = JwtTokenProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }


    @GetMapping
    public List<com.unipi.ItineraJava.model.User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<com.unipi.ItineraJava.model.User> getUserById(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteById(id);
    }

    ///////////////////////////////////modifiche Bache/////////////////////////////////////////////////////////


    // http://localhost:8080/users/find/test
    @GetMapping("/find/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("Authenticated user: " + authentication.getName());
            return userService.findByUsername(username);
        }
        throw new AccessDeniedException("User not authenticated");
    }


    // Endpoint per aggiornare il campo "reported" per uno specifico username
    @PutMapping("/report/{username}")
    public void reportUser(@PathVariable String username, @RequestParam boolean reported) {
        userService.updateReportedByUsername(username, reported);
    }
}