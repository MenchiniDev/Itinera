package com.unipi.lsmsd.itinera;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
//import org.neo4j.driver.AuthTokens;
//import org.neo4j.driver.Driver;
//import org.neo4j.driver.GraphDatabase;
//import org.neo4j.driver.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItineraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItineraApplication.class, args);

		// Connessione a MongoDB
		String mongoUri = "mongodb://localhost:27017"; // Sostituisci con l'URI corretto
		try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
			MongoDatabase database = mongoClient.getDatabase("itineraDB"); // Sostituisci con il nome del tuo database
			System.out.println("Connessione a MongoDB riuscita: " + database.getName());
		} catch (Exception e) {
			System.err.println("Errore nella connessione a MongoDB: " + e.getMessage());
			e.printStackTrace();
		}

		// Connessione a Neo4j
//		String neo4jUri = "bolt://localhost:7687"; // Sostituisci con l'URI corretto
//		String username = "neo4j"; // Sostituisci con il tuo username
//		String password = "password"; // Sostituisci con la tua password
//		try (Driver driver = GraphDatabase.driver(neo4jUri, AuthTokens.basic(username, password));
//			 Session session = driver.session()) {
//			String query = "RETURN 'Connessione a Neo4j riuscita'";
//			String result = session.run(query).single().get(0).asString();
//			System.out.println(result);
//		} catch (Exception e) {
//			System.err.println("Errore nella connessione a Neo4j: " + e.getMessage());
//			e.printStackTrace();
//		}
	}
}
