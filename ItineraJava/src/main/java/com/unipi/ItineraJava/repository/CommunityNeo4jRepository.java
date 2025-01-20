package com.unipi.ItineraJava.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.CommunityGraph;

@Repository
public interface CommunityNeo4jRepository extends Neo4jRepository<CommunityGraph, Long> {

    // Metodo per verificare se una community esiste
    @Query("MATCH (c:Community {city: $city}) RETURN COUNT(c) > 0")
    boolean existsByCity(String city);

    //Metodo per verificare che un utente abbia joinato una communtiy
    @Query("MATCH (u:User {username: $username})-[r:CONNECTED]->(c:Community {city: $city}) " +
       "RETURN COUNT(r) > 0")
    boolean isAlreadyJoined(String username, String city);


    // Metodo per creare la relazione JOINED tra utente e community
    @Query("MATCH (u:User {username: $username}), (c:Community {city: $city}) " +
           "MERGE (u)-[:CONNECTED]->(c)")
    void createJoinToCommunity(String username, String city);



    @Query("MATCH (u:User {username: $username})-[r:CONNECTED]->(c:Community {city: $city})\n" +
                "DELETE r")
    void deleteJoinToCommunity(String username, String city); 
}
