package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Reviews")
public class Review {
    @Id
    private String id;
    private String place_id;
    private String user;
    private int stars;
    private String text;
    private String timestamp;
    private boolean reported;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlace_id() { return place_id; }
    public void setPlace_id(String place_id) { this.place_id = place_id; }
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
