package com.unipi.ItineraJava.DTO;

import com.unipi.ItineraJava.model.PostSummary;

import java.util.Collections;
import java.util.List;

public class CommunityDTO {
    private String city;
    private String name;
    private List<PostSummary> post;

    public CommunityDTO() {}

    public CommunityDTO(String city, String name) {
        this.city = city;
        this.name = name;
        this.post = Collections.singletonList(new PostSummary());
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

    public List<PostSummary> getPost() { // Cambiato da "getSummary" a "getPost"
        return post;
    }

    public void setPost(List<PostSummary> post) { // Cambiato da "setSummary" a "setPost"
        this.post = post;
    }
}
