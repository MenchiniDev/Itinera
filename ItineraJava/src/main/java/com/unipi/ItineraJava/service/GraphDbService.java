package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.graphdb.CreateGraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GraphDbService {

    public boolean isUserJoinedCommunity(String username, String communityId) {
        try (Session session = CreateGraphDatabase.getNeo4jSession()) {
            String query = """
            MATCH (u:User {username: $username})-[:JOINED]->(c:Community {id: $communityId})
            RETURN COUNT(*) > 0 AS isJoined
        """;

            Map<String, Object> parameters = Map.of("username", username, "communityId", communityId);

            var result = session.run(query, parameters);

            // finding if there are or not
            if (result.hasNext()) {
                return result.next().get("isJoined").asBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
