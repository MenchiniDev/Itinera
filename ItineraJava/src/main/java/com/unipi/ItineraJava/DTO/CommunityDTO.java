package com.unipi.ItineraJava.DTO;

public class CommunityDTO {
    private String city;

    public CommunityDTO(String city) {
        this.city = city;
    }

    // Getter
    public String getCity() {
        return city;
    }

    // Setter
    public void setCity(String city) {
        this.city = city;
    }
}
