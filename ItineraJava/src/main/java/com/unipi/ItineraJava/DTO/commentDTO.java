package com.unipi.ItineraJava.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class commentDTO {

    @JsonProperty("community")
    private String community;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("comment")
    private String comment;

    public commentDTO() {}

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

    public void setComment(String comment) {
        this.comment = comment;
    }
}
