package com.unipi.ItineraJava.model;

import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class CommentRelationship {

    @Property("timestamp")
    private String timestamp; // Attributo della relazione

    @TargetNode
    private PostGraph post; // Nodo di destinazione

    public CommentRelationship(String timestamp, PostGraph post) {
        this.timestamp = timestamp;
        this.post = post;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public PostGraph getPost() {
        return post;
    }

    public void setPost(PostGraph post) {
        this.post = post;
    }
}
