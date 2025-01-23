package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

// POST
@Document(collection = "Post")
public class Post {
    @Id
    private String id;

    private Long postId;
    @Field("community")
    private String community;
    @Field("username")
    private String username;
    @Field("post")
    private String post;
    @Field("timestamp")
    private String timestamp;
    @Field("ncomment")
    private int ncomment;
    @Field("reportedpost")
    private boolean reportedpost; //se reported è true l'admin deciderà se eliminarlo o no, in caso contrario torna a false
    @Field("comment")
    private List<Comment> comment;

    public Post() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
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

