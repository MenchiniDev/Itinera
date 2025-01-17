package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

// COMMUNITY
@Document(collection = "communities")
public class MongoCommunity {
    @Id
    private String id;
    private String city;
    private String name;
    private List<Post> post;

    public List<Post> getPosts() {
        return post;
    }

    // Getters and Setters
}

