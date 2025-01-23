package com.unipi.ItineraJava.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.DTO.*;

import com.unipi.ItineraJava.model.UserGraph;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserGraph, String> {


       @Query("MATCH (u:User {username: $username}) RETURN count(u) > 0")
       boolean existsByUsername(String username);

       @Query("MATCH (u:User {username: $username})-[:FOLLOWING]->(u2:User {username: $userToFollow}) RETURN count(u) > 0")
       boolean existsFollowRelationship(String username, String userToFollow);

    //creazione nuovo nodo username alla signup
       @Query("CREATE (u:User {username: $username}) RETURN u")
       void createUserNode(String username);

       @Query("MATCH (u:User {username: $username}) DETACH DELETE u")
       void deleteUserNode(String username);

    //lista delle community che l'utente loggato segue
       @Query("MATCH (u:User {username: $username})-[:CONNECTED]->(c:Community) RETURN c.city AS city")
       List<CommunityDTO>  getCommunityJoined(String username);

       @Query("MATCH (u1:User {username: $username}), (u2:User {username: $userToFollow}) " +
              "MERGE (u1)-[:FOLLOWING]->(u2)")
       void followUser(String username, String userToFollow);

       @Query("MATCH (u:User {username: $username})-[f:FOLLOWING]->(u2:User {username: $userToUnfollow}) DELETE f")
       void unfollowUser(String username, String userToUnfollow);


       @Query("MATCH (u:User {username: $username})-[:FOLLOWING]->(u2:User) RETURN u2")
       List<UserDTO> getFollowing(String username);


       @Query("MATCH (u:User {username: $username}) " +
           "OPTIONAL MATCH (u)-[:FOLLOWING]->(f:User)<-[:FOLLOWING]-(suggested1:User) " +
           "WHERE NOT (u)-[:FOLLOWING]->(suggested1) AND u <> suggested1 " +
           "WITH u, COLLECT(DISTINCT suggested1.username) AS firstLevelSuggestions " +
           "OPTIONAL MATCH (u)-[:CONNECTED]->(:Community)<-[:CONNECTED]-(suggested2:User) " +
           "WHERE NOT (u)-[:FOLLOWING]->(suggested2) AND u <> suggested2 " +
           "WITH u, firstLevelSuggestions, COLLECT(DISTINCT suggested2.username) AS secondLevelSuggestions " +
           "OPTIONAL MATCH (suggested3:User) " +
           "WHERE NOT (u)-[:FOLLOWING]->(suggested3) AND u <> suggested3 " +
           "WITH firstLevelSuggestions AS level1, secondLevelSuggestions AS level2, " +
           "     COLLECT(DISTINCT suggested3.username) AS level3 " +
           "WITH level1 + level2 + level3 AS finalSuggestions " +
           "UNWIND finalSuggestions AS suggestedUsername " +
           "RETURN DISTINCT suggestedUsername " +
           "LIMIT 10")
       List<String> findSuggestedUsernames(@Param("username") String username);



       @Query("MATCH (u:User {username: $username}) " +
           "OPTIONAL MATCH (u)-[:FOLLOWING]->(f:User)-[:CONNECTED]->(suggestedCommunity:Community) " +
           "WHERE NOT (u)-[:CONNECTED]->(suggestedCommunity) " +
           "WITH u, COLLECT(DISTINCT suggestedCommunity.city) AS suggestedByFollowedUsers " +
           "OPTIONAL MATCH (randomCommunity:Community) " +
           "WHERE NOT (u)-[:CONNECTED]->(randomCommunity) " +
           "WITH suggestedByFollowedUsers AS level1, COLLECT(DISTINCT randomCommunity.city) AS level2 " +
           "WITH level1 + level2 AS allSuggestions " +
           "UNWIND allSuggestions AS suggestedCommunityName " +
           "RETURN DISTINCT suggestedCommunityName " +
           "LIMIT 5")
       List<String> findSuggestedCommunities(@Param("username") String username);


       @Query("""
    MATCH (u:User {username: $username})
    OPTIONAL MATCH (u)-[:FOLLOWING]->(f:User)-[:ASSOCIATED]->(p:Post)
    OPTIONAL MATCH (p)-[:ASSOCIATED]->(c:Community)
    WITH collect({ post: p, community: c }) AS pac

    CALL {
        WITH pac
        WITH pac, size([x IN pac WHERE x.post IS NOT NULL]) AS postCount
        WHERE postCount > 0

        UNWIND pac AS row
        WITH row.post AS post, row.community AS community
        WHERE post IS NOT NULL

        RETURN post.postId       AS postId,
               post.preview   AS preview,
               community.city AS community

        UNION

        WITH pac
        WITH pac, size([x IN pac WHERE x.post IS NOT NULL]) AS postCount
        WHERE postCount = 0

        MATCH (randomPost:Post)
        OPTIONAL MATCH (randomPost)-[:ASSOCIATED]->(rc:Community)
        WITH randomPost, rc, rand() AS r
        ORDER BY r
        LIMIT 10

        RETURN randomPost.postId      AS postId,
               randomPost.preview AS preview,
               rc.city            AS community
    }
    RETURN postId, preview, community
    """)
       List<PostSuggestionDto> findSuggestedPosts(@Param("username") String username);




}
