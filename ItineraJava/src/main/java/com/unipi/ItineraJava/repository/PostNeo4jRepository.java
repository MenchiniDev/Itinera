package com.unipi.ItineraJava.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.*;

public interface PostNeo4jRepository extends Neo4jRepository<PostGraph, Long>{
    

    @Query("CREATE (p:Post {id: $id, preview: $preview, timestamp: $timestamp}) RETURN p")
    void createPostNode(Long id, String preview, String timestamp);

    @Query("MATCH (p:Post {id: $id}) DETACH DELETE p")
    void deletePostNode(Long id);

    @Query("MATCH (u:User {username: $username}), (p:Post {postId: $postId}) " +
        "CREATE (u)-[c:COMMENT {timestamp: $timestamp}]->(p) " +
        "RETURN c")
    void addCommentToPost(Long postId, String timestamp, String username);

    @Query("MATCH (u:User {username: $username})-[c:COMMENT]->(p:Post {postId: $postId}) DETACH DELETE c")
    void deleteComment(String username, String postId);
}
