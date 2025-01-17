package com.unipi.ItineraJava;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.unipi.ItineraJava.graphdb.CreateGraphDatabase;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItineraJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItineraJavaApplication.class, args);

		// Connessione a MongoDB
		try (MongoClient mongoClient = createMongoConnection("mongodb://localhost:27017", "itineraDB")) {
			System.out.println("Connessione a MongoDB riuscita: " + mongoClient.getDatabase("itineraDB").getName());
		} catch (Exception e) {
			System.err.println("Errore nella connessione a MongoDB: " + e.getMessage());
			e.printStackTrace();
		}

		// Connessione a Neo4j
		try(var connection = CreateGraphDatabase.getNeo4jSession()) {
			System.out.println("Connessione a Neo4j riuscita");
		} catch (Exception e) {
			System.err.println("Errore nella connessione a Neo4j: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
