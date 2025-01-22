package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

// POST
@Document(collection = "Post")
public class Post {
    @Id
    private String id;
    private String community;
    private String username;
    private String post;
    private String timestamp;
    private int ncomment;
    private boolean reportedpost; //se reported è true l'admin deciderà se eliminarlo o no, in caso contrario torna a false
    private List<Comment> comment;

    public Post() {}
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
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public int getNum_comment() {
        return ncomment;
    }
    public void setNum_comment(int num_comment) {
        this.ncomment = num_comment;
    }
    public boolean isReported_post() {
        return reportedpost;
    }
    public void setReported_post(boolean reported_post) {
        this.reportedpost = reported_post;
    }
    public List<Comment> getComment() {
        return comment;
    }
    public void setComment(List<Comment> commenti) {
        this.comment = commenti;
    }


}

