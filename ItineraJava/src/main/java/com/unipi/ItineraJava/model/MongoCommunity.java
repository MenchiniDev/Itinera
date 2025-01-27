package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Community")
public class MongoCommunity {
    @Id
    private String id;
    private String city;
    private String name;
    private String created;
    private List<PostSummary> post;

    // Getters and Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = String.valueOf(created);
    }

    public List<PostSummary> getPost() {
        return post;
    }

    public void setPost(List<PostSummary> post) {
        this.post = post;
    }

    public List<Post> getPosts() {
        return new ArrayList<Post>();
    }
}

