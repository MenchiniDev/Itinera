package com.unipi.ItineraJava.DTO;

public class CommunityDTO {
    private String city;
    

    public CommunityDTO() {
    }

    public CommunityDTO(String city, String name) {
        this.city = city;
        
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
