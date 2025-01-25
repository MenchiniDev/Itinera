package com.unipi.ItineraJava.DTO;

public class ReviewsReportedDTO {
    private String place_id;
    private String user;
    private String text;
    private String timestamp;

    // Costruttore
    public ReviewsReportedDTO(String place_id, String user, String text, String timestamp) {
        this.place_id = place_id;
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
