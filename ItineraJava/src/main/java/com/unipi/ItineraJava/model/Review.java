package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
public class Review {

    @Id
    private String id;
    private String placeId; // ID del place a cui è associata la recensione
    private String user;
    private int stars;
    private String text;
    private String timestamp;
    private boolean reported;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public boolean isReported() { return reported; }
    public void setReported(boolean reported) { this.reported = reported; }
}
