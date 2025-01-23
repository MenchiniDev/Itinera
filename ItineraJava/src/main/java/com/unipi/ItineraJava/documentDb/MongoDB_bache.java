package com.unipi.ItineraJava.documentDb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.util.List;

public class MongoDB_bache {
    public static void main(String[] args) {
        // Configurazione MongoDB
        String mongoUri = "mongodb://myUserAdmin:root@localhost:27017"; // URI di connessione
        String databaseName = "itineraDB"; // Nome del database

        // Liste di file JSON da importare
        List<String> collection1Files = List.of(
                "itinera/dataScraping/reviews/hotel_rev/Hotels.json",
                "itinera/dataScraping/reviews/monuments_rev/monuments.json",
                "itinera/dataScraping/reviews/restaurants_rev/restaurants.json",
                "itinera/dataScraping/reviews/museums_rev/museums.json" //aggiunto
        );

        List<String> collection2Files = List.of(
                "itinera/dataScraping/reviews/hotel_rev/ReviewsHotels.json",
                "itinera/dataScraping/reviews/monuments_rev/reviews_monuments.json",
                "itinera/dataScraping/reviews/restaurants_rev/reviews_restaurants.json",
                "itinera/dataScraping/reviews/museums_rev/museums_reviews.json" //aggiunto
        );

        List<String> collection3Files = List.of(
                "itinera/dataScraping/users_data/users.json"
        );

        // Nome delle collezioni MongoDB
        String collection1Name = "Places";
        String collection2Name = "Reviews";
        String collection3Name = "Users";

        // Connessione a MongoDB
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            // Importa i file per la prima collezione
            System.out.println("Importando dati nella collezione: " + collection1Name);
            importFilesToCollection(database, collection1Name, collection1Files);

            // Importa i file per la seconda collezione
            System.out.println("Importando dati nella collezione: " + collection2Name);
            importFilesToCollection(database, collection2Name, collection2Files);

            // Importa i file per la terza collezione
            System.out.println("Importando dati nella collezione: " + collection3Name);
            importFilesToCollection(database, collection3Name, collection3Files);

            System.out.println("Importazione completata per tutte le collezioni.");
        } catch (Exception e) {
            System.err.println("Errore generale durante l'importazione dei dati: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Importa i dati da una lista di file JSON in una collezione MongoDB.
     *
     * @param database      Database MongoDB.
     * @param collectionName Nome della collezione.
     * @param filePaths      Lista dei percorsi dei file JSON.
     */
    private static void importFilesToCollection(MongoDatabase database, String collectionName, List<String> filePaths) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        for (String filePath : filePaths) {
            System.out.println("Importando da file: " + filePath);

            // Controlla se il file esiste
            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                System.err.println("Il file non esiste o non è leggibile: " + filePath);
                continue;
            }

            try {
                // Usa ObjectMapper per leggere il file JSON
                ObjectMapper objectMapper = new ObjectMapper();
                List<Document> documents = objectMapper.readValue(file, new TypeReference<List<Document>>() {});

                // Inserisci i documenti nella collezione MongoDB
                collection.insertMany(documents);

                System.out.println("Importazione completata per: " + filePath);
                System.out.println("Documenti importati: " + documents.size());
            } catch (Exception e) {
                System.err.println("Errore durante l'importazione del file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
