package com.unipi.ItineraJava.DTO;

import com.unipi.ItineraJava.model.Comment;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "Post")
public class PostDTO {
    private String id;
    private String community;
    private String username;
    private String post;
    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp; // Campo come LocalDateTime
    private int ncomment;
    private boolean reported_post;
    private List<Comment> comment;

    public PostDTO() {}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCommunity() {
        return community;
    }
    public void setCommunity(String community) {
        this.community = community;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPost() {
        return post;
    }
    public void setPost(String post) {
        this.post = post;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public int getNcomment() {
        return ncomment;
    }
    public void setNcomment(int ncomment) {
        this.ncomment = ncomment;
    }
    public boolean isReported_post() {
        return reported_post;
    }
    public void setReported_post(boolean reported_post) {
        this.reported_post = reported_post;
    }
    public List<Comment> getComment() {
        return comment;
    }
    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }
}
