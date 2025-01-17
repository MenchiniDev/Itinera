package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.graphdb.CreateGraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GraphDbService {

    //ritorna true se l'user è gia in una community, altrimenti false
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

    public boolean JoinCommunity(String username, String communityId) {
        try (Session session = CreateGraphDatabase.getNeo4jSession()) {

            if(isUserJoinedCommunity(username, communityId)) {
                return false; //ritorniamo false, l'utente è gia nella community
            }

            String query = """
            MATCH (u:User {username: $username}), (c:Community {id: $communityId})
            MERGE (u)-[:JOINED]->(c)
            RETURN COUNT(*) > 0 AS isCreated
        """;

            Map<String, Object> parameters = Map.of("username", username, "communityId", communityId);

            var result = session.run(query, parameters);

            // checking if the relationship was created
            if (result.hasNext()) {
                return result.next().get("isCreated").asBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
