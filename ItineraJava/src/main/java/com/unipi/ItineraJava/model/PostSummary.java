package com.unipi.ItineraJava.model;

public class PostSummary {
    private String User;
    private String Text;
    private String Timestamp; // Cambiato Timestamp in Date

    // Getters and Setters
    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.Timestamp = String.valueOf(timestamp);
    }
}
