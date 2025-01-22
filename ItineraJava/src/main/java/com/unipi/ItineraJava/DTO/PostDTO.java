package com.unipi.ItineraJava.DTO;

import com.unipi.ItineraJava.model.Comment;

import java.util.Date;
import java.util.List;


public class PostDTO {
    private String id;
    private String community;
    private String username;
    private String post;
    private String timestamp;
    private int ncomment;
    private boolean reportedpost; //se reported è true l'admin deciderà se eliminarlo o no, in caso contrario torna a false
    private List<Comment> comment;


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

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getNcomment() {
        return ncomment;
    }

    public boolean isReported_post() {
        return reportedpost;
    }

    public List<Comment> getComment() {
        return comment;
    }
}
