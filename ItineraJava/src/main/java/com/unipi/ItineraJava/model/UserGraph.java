package com.unipi.ItineraJava.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("User")
public class UserGraph {

    @Id
    @Property("username")
    private String username;

    @Relationship(type = "CONNECTED", direction = Relationship.Direction.OUTGOING)
    private CommunityGraph connectedCommunity;

    @Relationship(type = "ASSOCIATED", direction = Relationship.Direction.OUTGOING)
    private Set<PostGraph> posts;

    @Relationship(type = "COMMENT", direction = Relationship.Direction.OUTGOING)
    private Set<CommentRelationship> comments;

    @Relationship(type = "FOLLOWING", direction = Relationship.Direction.OUTGOING)
    private Set<UserGraph> following;

    public UserGraph() {}

    public UserGraph(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommunityGraph getConnectedCommunity() {
        return connectedCommunity;
    }

    public void setConnectedCommunity(CommunityGraph connectedCommunity) {
        this.connectedCommunity = connectedCommunity;
    }

    public Set<PostGraph> getPosts() {
        return posts;
    }

    public void setPosts(Set<PostGraph> posts) {
        this.posts = posts;
    }

    public Set<CommentRelationship> getComments() {
        return comments;
    }

    public void setComments(Set<CommentRelationship> comments) {
        this.comments = comments;
    }

    public Set<UserGraph> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserGraph> following) {
        this.following = following;
    }





}
