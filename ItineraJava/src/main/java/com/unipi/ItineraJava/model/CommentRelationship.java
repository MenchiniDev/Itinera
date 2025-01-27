package com.unipi.ItineraJava.model;

import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
public class CommentRelationship {

    @Id
    @GeneratedValue
    private String internalId;

    @Property("commentId")
    private String commentId;

    @Property("timestamp")
    private String timestamp;

    @TargetNode
    private PostGraph post;

    // Costruttore
    public CommentRelationship(String commentId, String timestamp, PostGraph post) {
        this.commentId = commentId;
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