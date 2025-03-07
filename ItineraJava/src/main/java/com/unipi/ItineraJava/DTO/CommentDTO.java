package com.unipi.ItineraJava.DTO;

import java.time.LocalDateTime;

public class CommentDTO {

    private String _id;          
    private String username;     
    private String timestamp; 
    private String body;         
    private boolean reported;    
    private String postId;  


    public CommentDTO(String _id, String username, String timestamp, String body, boolean reported, String postId) {
        this._id = _id;
        this.username = username;
        this.timestamp = timestamp;
        this.body = body;
        this.reported = reported;
        this.postId = postId;
    }

    // Getter e Setter
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // toString() per una stampa leggibile
    @Override
    public String toString() {
        return "CommentDTO{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", body='" + body + '\'' +
                ", reported=" + reported +
                ", postId='" + postId + '\'' +
                '}';
    }
}
