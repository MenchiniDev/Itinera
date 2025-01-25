package com.unipi.ItineraJava.model;

import org.springframework.data.mongodb.core.mapping.Field;

// COMMENT
public class Comment {

    @Field("commentId")
    private String commentId;
    @Field("username")
    private String username;
    @Field("timestamp")
    private String timestamp;
    @Field("body")
    private String body;
    @Field("reported")
    private boolean reportedcomment;

    public Comment() {}
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public String getCommentId() {
        return commentId;
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
        return reportedcomment;
    }
    public void setReported(boolean reported) {
        this.reportedcomment = reported;
    }
}
