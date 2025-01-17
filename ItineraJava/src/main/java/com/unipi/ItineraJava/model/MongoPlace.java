package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Places")
public class MongoPlace {

    @Id
    private String id;
    private String city;
    private String category; // hotel, restaurant, interest
    private String name;
    private String address;
    private List<PreReview> reviews; // embedded reviews

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

    public Double getAverageRating() {

        return (double) PreReview.getOverall_rating() /(double) reviews.size();
    }
}

class PreReview{
    private static double overall_rating;
    private int tot_rev_number;

    public static double getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(double overall_rating) {
        PreReview.overall_rating = overall_rating;
    }

    public void setTot_rev_number(int tot_rev_number) {
        this.tot_rev_number = tot_rev_number;
    }

    public int getTot_rev_number() {
        return tot_rev_number;
    }
}
