package com.unipi.ItineraJava.model;

// Necessary imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


// USERS
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String role; // "User" or "Admin"
    private LocalDateTime created;
    private boolean active;
    private boolean reported;
    private List<String> lastPost;

    // Getters and Setters
}

