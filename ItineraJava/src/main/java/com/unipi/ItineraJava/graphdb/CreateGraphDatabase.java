package com.unipi.ItineraJava.graphdb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class CreateGraphDatabase {

    private static final String NEO4J_URI = "bolt://10.1.1.23:7687";
    private static final String NEO4J_USERNAME = "neo4j"; //default
    private static final String NEO4J_PASSWORD = "12345678";

    public static Session getNeo4jSession() {
        Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD));
        return driver.session();
    }

    public static void main(String[] args) {
        // Path della cartella contenente i JSON dei post
        String postsFolderPath = "../dataScraping/Post_doc";

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD));
             Session session = driver.session()) {

            File folder = new File(postsFolderPath);
            File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("Nessun file JSON trovato nella cartella specificata.");
                return;
            }

            System.out.println("Trovati " + jsonFiles.length + " file JSON nella cartella.");

            // Set per tenere traccia di tutti gli utenti
            Set<String> users = new HashSet<>();

            for (File file : jsonFiles) {
                try {
                    System.out.println("Elaborazione file: " + file.getName());

                    // Leggo il contenuto del file JSON
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    JSONParser parser = new JSONParser();
                    JSONObject post = (JSONObject) parser.parse(content);

                    // Estraggo dati dal post
                    String postId = String.valueOf(post.get("postId"));
                    String communityName = (String) post.get("community");
                    String username = (String) post.get("username");
                    String preview = getPostPreview((String) post.get("post"));
                    String timestamp = (String) post.get("timestamp");

                    // Aggiungo l'utente al set di utenti
                    users.add(username);
                    System.out.println("Trovato utente: " + username + " e community: " + communityName);

                    // Assicuro che esistano i nodi Community e User
                    session.executeWrite(tx -> {
                        tx.run("MERGE (c:Community {city: $city})",
                                org.neo4j.driver.Values.parameters("city", communityName));
                        tx.run("MERGE (u:User {username: $username})",
                                org.neo4j.driver.Values.parameters("username", username));
                        return null;
                    });
                    System.out.println("Creati nodi Community e User per " + communityName + " e " + username);

                    // Creo il post e le relazioni con l'autore e la community
                    session.executeWrite(tx -> {
                        tx.run("CREATE (p:Post {postId: $postId, preview: $preview, timestamp: $timestamp}) " +
                                "WITH p " +
                                "MATCH (u:User {username: $username}), (c:Community {city: $city}) " +
                                "MERGE (u)-[:ASSOCIATED]->(p) " +
                                "MERGE (p)-[:ASSOCIATED]->(c)" +
                                "MERGE (u)-[:CONNECTED]->(c)",
                                org.neo4j.driver.Values.parameters(
                                        "postId", postId,
                                        "preview", preview,
                                        "timestamp", timestamp,
                                        "username", username,
                                        "city", communityName));
                        return null;
                    });
                    System.out.println("Creato nodo Post con id: " + postId + ", preview e timestamp associato a User e Community.");

                    // Estraggo i commenti e creo relazioni come archi
                    JSONArray comments = (JSONArray) post.get("comment");
                    System.out.println("Trovati " + comments.size() + " commenti nel file " + file.getName());

                    for (Object commentObj : comments) {
                        JSONObject comment = (JSONObject) commentObj;
                        String commentUsername = (String) comment.get("username");
                        String commentTimestamp = (String) comment.get("timestamp");

                        // Aggiungo l'utente del commento al set di utenti
                        users.add(commentUsername);

                        // Assicuro che esista il nodo User per il commento
                        session.executeWrite(tx -> {
                            tx.run("MERGE (u:User {username: $username})",
                                    org.neo4j.driver.Values.parameters("username", commentUsername));
                            return null;
                        });
                        System.out.println("Creato nodo User per commento di: " + commentUsername);

                        // Creo un arco COMMENT tra l'utente e il post
                        session.executeWrite(tx -> {
                            tx.run("MATCH (u:User {username: $username}), (p:Post {preview: $preview}) " +
                                    "MERGE (u)-[:COMMENT {timestamp: $timestamp}]->(p)",
                                    org.neo4j.driver.Values.parameters(
                                            "username", commentUsername,
                                            "preview", preview,
                                            "timestamp", commentTimestamp));
                            return null;
                        });
                        System.out.println("Creato arco COMMENT tra " + commentUsername + " e il Post.");

                        // Connetto il commentatore alla community del post
                        session.executeWrite(tx -> {
                            tx.run("MATCH (u:User {username: $username}), (c:Community {city: $city}) " +
                                    "MERGE (u)-[:CONNECTED]->(c)",
                                    org.neo4j.driver.Values.parameters(
                                            "username", commentUsername,
                                            "city", communityName));
                            return null;
                        });
                        System.out.println("Creato arco CONNECTED tra " + commentUsername + " e la Community " + communityName);
                    }

                } catch (Exception e) {
                    System.err.println("Errore durante l'elaborazione del file: " + file.getName());
                    e.printStackTrace();
                }
            }

            System.out.println("Processamento dei file JSON completato. Creazione delle relazioni FOLLOWING in corso...");
            createRandomFollows(session, users);
            System.out.println("Relazioni FOLLOWING generate casualmente.");

        } catch (Exception e) {
            System.err.println("Errore durante la connessione al database Neo4j.");
            e.printStackTrace();
        }
    }

    /**
     * Metodo per creare una preview del corpo del post.
     */
    private static String getPostPreview(String postBody) {
        if (postBody == null || postBody.isEmpty()) {
            return "No preview available...";
        }
        return postBody.length() > 32 ? postBody.substring(0, 29) + "..." : postBody;
    }

    /**
     * Creo relazioni FOLLOWING tra utenti in modo casuale.
     */
    private static void createRandomFollows(Session session, Set<String> users) {
        System.out.println("Inizio creazione relazioni FOLLOWING...");

        List<String> userList = new ArrayList<>(users);

        // Utilizzo un seed fisso per ottenere risultati consistenti tra esecuzioni
        Random random = new Random(42); 

        int totalFollowsCreated = 0;

        for (String user : userList) {
            // Numero casuale di utenti da seguire
            int numFollows = random.nextInt(25) + 1; // Tra 1 e 25 utenti seguiti

            System.out.println("Utente: " + user + " seguirà " + numFollows + " utenti.");

            for (int i = 0; numFollows > i; i++) {
                String targetUser = userList.get(random.nextInt(userList.size()));

                // Evita che un utente segua se stesso
                if (!user.equals(targetUser)) {
                    session.executeWrite(tx -> {
                        tx.run("MATCH (u1:User {username: $user}), (u2:User {username: $targetUser}) " +
                                "MERGE (u1)-[:FOLLOWING]->(u2)",
                                org.neo4j.driver.Values.parameters("user", user, "targetUser", targetUser));
                        return null;
                    });
                    totalFollowsCreated++;

                    // Stampa di controllo per ogni relazione creata
                    System.out.println("Creata relazione FOLLOWING: " + user + " → " + targetUser);
                }
            }
        }

        System.out.println("Relazioni FOLLOWING completate. Totale relazioni create: " + totalFollowsCreated);
    }
}
