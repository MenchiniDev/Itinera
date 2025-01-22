package com.unipi.ItineraJava.model;

import java.time.LocalDateTime;

// COMMENT
public class Comment {
    private String user;
    private String timestamp;
    private String text;
    private boolean reported;

    public Comment() {}
    public Comment(String user, String  timestamp, String text, boolean reported) {
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
    public String  getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String  timestamp) {
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
