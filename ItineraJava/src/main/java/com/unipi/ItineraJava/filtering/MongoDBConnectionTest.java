package com.unipi.ItineraJava.filtering;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
//String mongoUri = "mongodb://myUserAdmin:root@localhost:27017/itineraDB?authSource=admin";

import com.mongodb.client.MongoDatabase;

public class MongoDBConnectionTest {
    public static void main(String[] args) {
        // URI di connessione
        String mongoUri = "mongodb://myUserAdmin:root@localhost:27017"; // Modifica come necessario

        try (MongoClient client = MongoClients.create(mongoUri)) {
            // Verifica connessione
            System.out.println("Connesso con successo al database MongoDB!");

            // Prova ad accedere al database 'itineraDB'
            MongoDatabase database = client.getDatabase("itineraDB");
            System.out.println("Connessione al database 'itineraDB' riuscita!");

            // Lista le collezioni nel database per verifica
            database.listCollectionNames().forEach(System.out::println);
        } catch (Exception e) {
            // Gestione degli errori
            System.err.println("Errore di connessione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
