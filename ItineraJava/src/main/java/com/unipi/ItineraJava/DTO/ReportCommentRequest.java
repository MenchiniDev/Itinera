package com.unipi.ItineraJava.DTO;

public class ReportCommentRequest {
    private String user;
    private String community;
    private String textComment;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getTextComment() {
        return textComment;
    }
    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }
}

