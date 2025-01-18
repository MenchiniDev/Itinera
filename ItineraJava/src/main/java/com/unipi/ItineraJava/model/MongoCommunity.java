package com.unipi.ItineraJava.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "Community")
public class MongoCommunity {
    @Id
    private String id; // String per l'ID
    private String City;
    private String Name;
    private String Created;  // Usa Date al posto di Timestamp
    private List<PostSummary> Post;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        this.City = city;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(Date created) {
        this.Created = String.valueOf(created);
    }

    public List<PostSummary> getPost() {
        return Post;
    }

    public void setPost(List<PostSummary> post) {
        this.Post = post;
    }

    public List<Post> getPosts() {
        return new ArrayList<Post>();
    }
}

class PostSummary {
    private String User;
    private String Text;
    private String Timestamp; // Cambiato Timestamp in Date

    // Getters and Setters
    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        this.User = user;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.Timestamp = String.valueOf(timestamp);
    }
}
