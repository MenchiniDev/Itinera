package com.unipi.ItineraJava.model;

import org.springframework.data.mongodb.core.mapping.Field;

// COMMENT
public class Comment {

    @Field("_id")
    private String _id;
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
        this._id = _id;
    }
    public String getCommentId() {
        return _id;
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
