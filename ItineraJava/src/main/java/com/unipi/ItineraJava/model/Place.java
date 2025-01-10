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

