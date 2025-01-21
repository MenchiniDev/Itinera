package com.unipi.ItineraJava.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.DTO.CommunityDTO;
import com.unipi.ItineraJava.DTO.UserDTO;
import com.unipi.ItineraJava.model.CommunityGraph;
import com.unipi.ItineraJava.model.UserGraph;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserGraph, Long> {


    @Query("MATCH (u:User {username: $username}) RETURN count(u) > 0")
    boolean existsByUsername(String username);

    @Query("MATCH (u:User {username: $user})-[:FOLLOWING]->(u2:User {username: $userToFollow}) RETURN count(u) > 0")
    boolean existsFollowRelationship(String user, String userToFollow);

    //creazione nuovo nodo user alla signup
    @Query("CREATE (u:User {username: $username}) RETURN u")
    void createUserNode(String username);

    //lista delle community che l'utente loggato segue
    @Query("MATCH (u:User {username: $username})-[:CONNECTED]->(c:Community) RETURN c.city AS city")
    List<CommunityDTO>  getCommunityJoined(String username);

    @Query("MATCH (u1:User {username: $user}), (u2:User {username: $userToFollow}) " +
                "MERGE (u1)-[:FOLLOWING]->(u2)")
    void followUser(String user, String userToFollow);

    @Query("MATCH (u:User {username: $user})-[f:FOLLOWING]->(u2:User {username: $userToUnfollow}) DELETE f")
    void unfollowUser(String user, String userToUnfollow);


    @Query("MATCH (u:User {username: $username})-[:FOLLOWING]->(u2:User) RETURN u2")
    List<UserDTO> getFollowing(String username);
}
