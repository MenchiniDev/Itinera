package com.unipi.ItineraJava.model;

// Necessary imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unipi.ItineraJava.service.UserService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;


// USERS
@Document(collection = "Users")
@Component
@JsonIgnoreProperties({"jwtTokenProvider"})
public class User {
    private static JwtTokenProvider jwtTokenProvider;
    @Autowired
    public void setJwtTokenProvider(JwtTokenProvider provider) {
        jwtTokenProvider = provider;
    }

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String role; // "User" or "Admin"
    private String created;
    private boolean active;
    private boolean reported;
    private Last_post last_post;



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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setCreated(String created) {
        this.created = created;
    }
    public String getCreated() {
        return created;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    public void setReported(boolean reported) {
        this.reported = reported;
    }
    public boolean isReported() {
        return reported;
    }

    public void setLastPost(Last_post last_post) {
        this.last_post = last_post;
    }
    public Last_post getLastPost() {
        return last_post;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public static boolean isAdmin(String token) {
        try {
            String jwt = token.replace("Bearer ", "").trim();

            String username = JwtTokenProvider.getUsernameFromToken(jwt);
            System.out.println("Username: " + username);

            User user = UserService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            System.out.println("Role: " + user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                System.out.println("L'utente è ADMIN");
                return true;
            } else {
                System.out.println("L'utente NON è ADMIN");
                return false;
            }

        } catch (Exception e) {
            System.out.println("L'user NON è ADMIN");
            e.printStackTrace();
            return false;
        }
    }
}


