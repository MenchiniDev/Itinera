package com.unipi.ItineraJava.model;

import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
public class CommentRelationship {

    // Aggiungi un campo id generato automaticamente
    @Id
    @GeneratedValue
    private String internalId; // ID generato internamente per la relazione

    @Property("commentId")
    private String commentId; // ID personalizzato

    @Property("timestamp")
    private String timestamp; // Attributo della relazione

    @TargetNode
    private PostGraph post; // Nodo di destinazione

    // Costruttore
    public CommentRelationship(String commentId, String timestamp, PostGraph post) {
        this.commentId = commentId; // ID personalizzato
        this.timestamp = timestamp;
        this.post = post;
    }

    // Getter e setter
    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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