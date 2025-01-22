package com.unipi.ItineraJava.DTO;

public class commentDTO {

    private String community;
    private String timestamp;
    private final String comment;

    public commentDTO(String community, String timestamp, String comment) {
        this.community = community;
        this.timestamp = timestamp;
        this.comment = comment;
    }
    public String getCommunity() {
        return community;
    }
    public void setCommunity(String community) {
        this.community = community;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getComment() {
        return comment;
    }
}
