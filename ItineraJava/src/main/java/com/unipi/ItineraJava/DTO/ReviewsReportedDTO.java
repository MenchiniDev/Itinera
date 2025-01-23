package com.unipi.ItineraJava.DTO;

public class ReviewsReportedDTO {
    private String place_name;
    private String user;
    private String text;
    private String timestamp;

    // Costruttore
    public ReviewsReportedDTO(String place_name, String user, String text, String timestamp) {
        this.place_name = place_name;
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters e Setters
    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
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
