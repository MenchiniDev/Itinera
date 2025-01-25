package com.unipi.ItineraJava.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.*;

public interface PostNeo4jRepository extends Neo4jRepository<PostGraph, String>{
    

    @Query("MERGE (u:User {username: $username}) " +
       "MERGE (c:Community {city: $community}) " +
       "CREATE (p:Post {postId: $postId, preview: $preview, timestamp: $timestamp}) " +
       "MERGE (u)-[:ASSOCIATED]->(p) " +
       "MERGE (p)-[:ASSOCIATED]->(c) " +
       "RETURN p")
    void createPostNode(String postId, String preview, String timestamp, String username, String community);

    @Query("MATCH (p:Post {postId: $id}) DETACH DELETE p")
    void deletePostNode(String id);

    /* 
    @Query("MATCH (u:User {username: $username}), (p:Post {postId: $postId}) " +
        "CREATE (u)-[c:COMMENT {timestamp: $timestamp}]->(p) " +
        "RETURN c")
    void addCommentToPost(String postId, String timestamp, String username);    //VA MODIFICATA AGGIUNGENDO IL COMMENT ID 
    */

    @Query("MATCH (u:User {username: $username}), (p:Post {postId: $postId}) " +
       "CREATE (u)-[c:COMMENT {commentId: $commentId, timestamp: $timestamp}]->(p) " +
       "RETURN c")
    void addCommentToPost(String postId, String timestamp, String username, String commentId);

    /* 
    @Query("MATCH (u:User {username: $username})-[c:COMMENT {timestamp: $timestamp}]->(p:Post {postId: $postId}) " +
        "DETACH DELETE c")
    void deleteComment(String username, String postId, String timestamp); //VA MODIFICATA CON COMMENT ID 
    */

    @Query("MATCH (u:User)-[c:COMMENT {commentId: $commentId}]->(p:Post) " +
        "DETACH DELETE c")
    void deleteComment(String commentId);

}
