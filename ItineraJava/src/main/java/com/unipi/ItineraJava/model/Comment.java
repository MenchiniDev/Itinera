package com.unipi.ItineraJava.model;
// COMMENT
public class Comment {
    private String username;
    private String timestamp;
    private String body;
    private boolean reported;

    public Comment() {}
    public Comment(String user, String  timestamp, String text, boolean reported) {
        this.username = user;
        this.timestamp = timestamp;
        this.body = text;
        this.reported = reported;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String  getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String  timestamp) {
        this.timestamp = timestamp;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public boolean isReported() {
        return reported;
    }
    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
