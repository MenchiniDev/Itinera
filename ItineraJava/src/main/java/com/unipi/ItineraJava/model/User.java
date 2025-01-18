package com.unipi.ItineraJava.model;

// Necessary imports
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;


// USERS
@Document(collection = "Users")
public class User {

    @Id
    private String Id;
    private String username;
    private String Email;
    private String Password;
    private String Role; // "User" or "Admin"
    private String Created;
    private boolean Active;
    private boolean Reported;
    private List<String> LastPost;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        this.Role = role;
    }

    public void setEmail(String email) {
        this.Email = email;
    }
    public String getEmail() {
        return Email;
    }
    public void setCreated(String created) {
        this.Created = created;
    }
    public String getCreated() {
        return Created;
    }
    public void setActive(boolean active) {
        this.Active = active;
    }
    public boolean isActive() {
        return Active;
    }
    public void setReported(boolean reported) {
        this.Reported = reported;
    }
    public boolean isReported() {
        return Reported;
    }
    public void setLastPost(List<String> lastPost) {
        this.LastPost = lastPost;
    }
    public List<String> getLastPost() {
        return LastPost;
    }
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        this.Id = id;
    }

}

