package com.unipi.ItineraJava.model;


import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Post")
public class PostGraph {

    @Id
    @Property("postId")
    private Long postId;

    @Property("preview")
    private String preview;

    @Property("timestamp")
    private String timestamp;

    @Relationship(type = "ASSOCIATED", direction = Relationship.Direction.OUTGOING)
    private CommunityGraph community;

    @Relationship(type = "ASSOCIATED", direction = Relationship.Direction.INCOMING)
    private UserGraph author;

    public PostGraph() {}

    public PostGraph(String preview, String timestamp) {
        this.preview = preview;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return postId;
    }

    public void setId(Long id) {
        this.postId = id;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CommunityGraph getCommunity() {
        return community;
    }

    public void setCommunity(CommunityGraph community) {
        this.community = community;
    }

    public UserGraph getAuthor() {
        return author;
    }

    public void setAuthor(UserGraph author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "PostGraph{" +
            "id=" + postId +
            ", preview='" + preview + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
    }

}
