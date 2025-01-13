package com.unipi.ItineraJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GraphDbService {

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    public boolean isUserJoinedCommunity(String username, String communityId) {
        String query = """
            MATCH (u:User {username: $username})-[:JOINED]->(c:Community {id: $communityId})
            RETURN COUNT(*) > 0 AS isJoined
        """;

        Map<String, Object> parameters = Map.of("username", username, "communityId", communityId);

        return neo4jTemplate.findOne(query, parameters, Boolean.class)
                .orElse(false);
    }
}
