package com.unipi.ItineraJava;

import com.mongodb.client.MongoClient;
import com.unipi.ItineraJava.graphdb.CreateGraphDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.unipi.ItineraJava.documentDb.MongoDBUploader.getMongoConnection;

@SpringBootApplication
public class ItineraJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItineraJavaApplication.class, args);

		// esempi di tentativi di connessione
		// Connessione a MongoDB
		try (MongoClient mongoClient = getMongoConnection("mongodb://localhost:27017", "itineraDB")) {
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
