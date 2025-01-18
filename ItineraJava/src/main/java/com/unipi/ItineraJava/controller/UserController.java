package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.User;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
class UserController {
    @Autowired
    private UserService userService;

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


    // Endpoint per trovare un utente per username
    @GetMapping("/find/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    // Endpoint per aggiornare il campo "reported" per uno specifico username
    @PutMapping("/report/{username}")
    public void reportUser(@PathVariable String username, @RequestParam boolean reported) {
        userService.updateReportedByUsername(username, reported);
    }


}