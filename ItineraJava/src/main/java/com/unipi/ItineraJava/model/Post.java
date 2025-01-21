package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

// POST
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String Community_name;
    private String username;
    private String post_body;
    private String timestamp;
    private int num_comment;
    private boolean reported_post; //se reported è true l'admin deciderà se eliminarlo o no, in caso contrario torna a false
    private List<Comment> comment;

    public Post() {}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCommunity_name() {
        return Community_name;
    }
    public void setCommunity_name(String community_name) {
        Community_name = community_name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPost_body() {
        return post_body;
    }
    public void setPost_body(String post_body) {
        this.post_body = post_body;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public int getNum_comment() {
        return num_comment;
    }
    public void setNum_comment(int num_comment) {
        this.num_comment = num_comment;
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
    public void setComment(List<Comment> commenti) {
        this.comment = commenti;
    }


}

