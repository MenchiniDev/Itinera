package com.unipi.ItineraJava.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.unipi.ItineraJava.model.UserGraph;


public interface UserNeo4jRepository extends Neo4jRepository<UserGraph, Long> {

    @Query("CREATE (u:User {username: $username}) RETURN u")
    void createUserNode(String username);
}
