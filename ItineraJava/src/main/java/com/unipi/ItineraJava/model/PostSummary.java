package com.unipi.ItineraJava.model;

public class PostSummary {
    private String user;
    private String text;
    private String timestamp;

    // Getters and Setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = String.valueOf(timestamp);
    }
}
