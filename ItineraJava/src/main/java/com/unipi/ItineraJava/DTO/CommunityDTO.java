package com.unipi.ItineraJava.DTO;

public class CommunityDTO {
    private String city;
    private String name;

    public CommunityDTO() {
    }

    public CommunityDTO(String city, String name) {
        this.city = city;
        this.name = city;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



}
