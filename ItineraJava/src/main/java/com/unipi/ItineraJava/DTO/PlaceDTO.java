package com.unipi.ItineraJava.DTO;

import com.unipi.ItineraJava.model.ReviewSummary;
import org.springframework.data.annotation.Id;

public class PlaceDTO {

    private String name;
    private String address;
    private String city;
    private String category; // Hotel, Restaurant, Monument

    public PlaceDTO() {}

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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
}
