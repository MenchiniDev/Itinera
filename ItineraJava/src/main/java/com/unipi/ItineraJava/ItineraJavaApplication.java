package com.unipi.ItineraJava;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
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
/*
		try (Driver driver = createNodeConnection("bolt://localhost:7687", "neo4j", "12345678");
			 Session session = driver.session()) {
			String query = "RETURN 'Connessione a Neo4j riuscita'";
			String result = session.run(query).single().get(0).asString();
			System.out.println(result);
		} catch (Exception e) {
			System.err.println("Errore nella connessione a Neo4j: " + e.getMessage());
			//e.printStackTrace();
		}*/
	}

	/**
	 * connessione a MongoDB.
	 *
	 * @param mongoUri URI di connessione a MongoDB
	 * @param databaseName Nome del database
	 * @return un oggetto MongoClient
	 */

	public static MongoClient createMongoConnection(String mongoUri, String databaseName) {
		MongoClient mongoClient = MongoClients.create(mongoUri);
		MongoDatabase database = mongoClient.getDatabase(databaseName);
		return mongoClient;
	}

	/**
	 * connessione a Neo4j.
	 *
	 * @param uri      URI di connessione a Neo4j
	 * @param username Username di autenticazione
	 * @param password Password di autenticazione
	 * @return un oggetto Driver per Neo4j
	 *//*
	public static Driver createNodeConnection(String uri, String username, String password) {
		Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
		System.out.println("Connessione a Neo4j creata con successo");
		return driver;
	}*/
}
