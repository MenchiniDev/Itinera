package com.unipi.ItineraJava.DTO;

public class SignupRequest {
    private String Username;
    private String Email;
    private String Password;

    // Getters e Setters
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }
}


