package com.unipi.ItineraJava.DTO;

public class PostSummaryDto {
    private String id;
    private String community;
    private String username;
    private String post;
    private int reportedComments;
    private String timestamp;


    public PostSummaryDto(String id, String community, String username, String post, int reportedComments, String timestamp) {
        this.id = id;
        this.community = community;
        this.username = username;
        this.post = post;
        this.reportedComments = reportedComments;
        this.timestamp = timestamp;
    }
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
    public int getReportedComments() {
        return reportedComments;
    }
    public void setReportedComments(int reportedComments) {
        this.reportedComments = reportedComments;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
