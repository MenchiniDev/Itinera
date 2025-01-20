package com.unipi.ItineraJava.model;

import java.time.LocalDateTime;

// COMMENT
public class Comment {
    private String user;
    private LocalDateTime timestamp;
    private String text;
    private boolean reported;

    public Comment() {}
    public Comment(String user, LocalDateTime timestamp, String text, boolean reported) {
        this.user = user;
        this.timestamp = timestamp;
        this.text = text;
        this.reported = reported;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isReported() {
        return reported;
    }
    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
