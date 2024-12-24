package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Places")
public class Place {

    @Id
    private String id;
    private String city;
    private String category; // hotel, restaurant, interest
    private String name;
    private String address;
    private double overallRating;
    private int totalReviewNum;
    private List<Review> reviews; // embedded reviews

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOverallRating(double overallRating) {
        this.overallRating = overallRating;
    }

    public void setTotalReviewNum(int totalReviewNum) {
        this.totalReviewNum = totalReviewNum;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public int getTotalReviewNum() {
        return totalReviewNum;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }
}

public class Review {
    private String revId;
    private String user;
    private int stars;
    private String text;
    private long timestamp;
    private boolean reported;

    public String getRevId() {
        return revId;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getStars() {
        return stars;
    }

    public boolean isReported() {
        return reported;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setRevId(String revId) {
        this.revId = revId;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}

