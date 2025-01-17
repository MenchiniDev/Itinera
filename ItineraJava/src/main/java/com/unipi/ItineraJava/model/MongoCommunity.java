package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.List;

// COMMUNITY
@Document(collection = "Communities")
public class MongoCommunity {
    @Id
    private String id;
    private String City;
    private String Name;
    private Timestamp Created;
    private List<Post> Post;

    public List<Post> getPosts() {
        return Post;
    }

    public String getName() {
        return Name;
    }

    // Getters and Setters
}

