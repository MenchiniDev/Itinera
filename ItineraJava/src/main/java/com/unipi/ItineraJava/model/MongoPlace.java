package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Places")
public class MongoPlace {

    @Id
    private String id;
    private String name;
    private String address;
    private String city;
    private String category; // Hotel, Restaurant, Monument or Museum
    private ReviewSummary reviews_info;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ReviewSummary getReviews() {
        return reviews_info;
    }

    public void setReviews(ReviewSummary reviews) {
        this.reviews_info = reviews;
    }
}
