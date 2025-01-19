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
    private String city;
    private String name;
    private String created;  // Usa Date al posto di Timestamp
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
