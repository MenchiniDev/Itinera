package com.unipi.ItineraJava.DTO;
import java.time.LocalDateTime;

public class ReportPostRequest {
    private String body;
    private String user;
    private String community;

    // Getter e Setter
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

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
}
