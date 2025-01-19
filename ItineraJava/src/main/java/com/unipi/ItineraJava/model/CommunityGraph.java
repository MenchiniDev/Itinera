package com.unipi.ItineraJava.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node("Community")
public class CommunityGraph {

    @Id
    @GeneratedValue
    private long id;
    @Property("City")
    private String city;

    @Relationship(type = "CONNECTED", direction = Relationship.Direction.INCOMING)
    private Set<UserGraph> connectedUsers;

    @Relationship(type = "ASSOCIATED", direction = Relationship.Direction.INCOMING)
    private Set<PostGraph> posts;

    public CommunityGraph() {}

    public CommunityGraph(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<UserGraph> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(Set<UserGraph> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public Set<PostGraph> getPosts() {
        return posts;
    }

    public void setPosts(Set<PostGraph> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "CommunityGraph{" +
            "city='" + city + '\'' +
            ", connectedUsers=" + (connectedUsers != null ? connectedUsers.size() : "null") +
            ", posts=" + (posts != null ? posts.size() : "null") +
            '}';
}

}
