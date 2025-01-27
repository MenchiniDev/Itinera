package com.unipi.ItineraJava.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.CommunityGraph;

@Repository
public interface CommunityNeo4jRepository extends Neo4jRepository<CommunityGraph, Long> {

   @Query("CREATE (c:Community {city: $city}) RETURN c")
   void createCommunityNode(String city);

   @Query("MATCH (c:Community {city: $city}) DETACH DELETE c")
   void deleteCommunity(String city);

   @Query("MATCH (c:Community {city: $city}) RETURN COUNT(c) > 0")
   boolean existsByCity(String city);

   @Query("MATCH (u:User {username: $username})-[r:CONNECTED]->(c:Community {city: $city}) " +
      "RETURN COUNT(r) > 0")
   boolean isAlreadyJoined(String username, String city);

   @Query("MATCH (u:User {username: $username}), (c:Community {city: $city}) " +
         "MERGE (u)-[:CONNECTED]->(c)")
   void createJoinToCommunity(String username, String city);

    
   @Query("MATCH (u:User {username: $username})-[r:CONNECTED]->(c:Community {city: $city})\n" +
               "DELETE r")
   void deleteJoinToCommunity(String username, String city); 


   @Query("MATCH (c:Community {city: $city})<-[:CONNECTED]-(u:User) " +
      "OPTIONAL MATCH (u)-[:ASSOCIATED]->(p:Post)-[:ASSOCIATED]->(c) " +
      "OPTIONAL MATCH (u)-[:COMMENT]->(com:Post)-[:ASSOCIATED]->(c) " +
      "WITH u, COUNT(DISTINCT p) AS posts, COUNT(DISTINCT com) AS comments " +
      "RETURN u.username AS username " +
      "ORDER BY (posts + comments) DESC " +
      "LIMIT 1")
   String findMostActiveUserByCommunity(@Param("city") String city);


   @Query("MATCH (c:Community) " +
      "OPTIONAL MATCH (c)<-[:CONNECTED]-(u:User) " +
      "OPTIONAL MATCH (c)<-[:ASSOCIATED]-(p:Post) " +
      "OPTIONAL MATCH (p)<-[:COMMENT]-(uCommenter:User) " +
      "WITH c.city AS communityName, " +
      "     COUNT(DISTINCT u) AS users, " +
      "     COUNT(DISTINCT p) AS posts, " +
      "     COUNT(DISTINCT uCommenter) AS comments " +
      "RETURN communityName " +
      "ORDER BY (users + posts + comments) DESC " +
      "LIMIT 1")
   String findMostActiveCommunity();




   @Query("MATCH (c:Community {city: $city})<-[:ASSOCIATED]-(p:Post) " +
           "RETURN COUNT(p) AS postCount")
    Long countPostsInCommunity(@Param("city") String city);

}
