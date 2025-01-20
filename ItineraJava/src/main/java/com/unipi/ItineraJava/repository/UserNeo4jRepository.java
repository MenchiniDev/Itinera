package com.unipi.ItineraJava.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.DTO.CommunityDTO;
import com.unipi.ItineraJava.model.CommunityGraph;
import com.unipi.ItineraJava.model.UserGraph;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserGraph, Long> {

    //creazione nuovo nodo user alla signup
    @Query("CREATE (u:User {username: $username}) RETURN u")
    void createUserNode(String username);

    //lista delle community che l'utente loggato segue
    @Query("MATCH (u:User {username: $username})-[:CONNECTED]->(c:Community) RETURN c.city AS city")
    List<CommunityDTO>  getCommunityJoined(String username);
}
