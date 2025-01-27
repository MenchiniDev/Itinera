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
        String mongoUri = "mongodb://myUserAdmin:root@10.1.1.25:27017/itineraDB?authSource=admin&authMechanism=SCRAM-SHA-256";
        String databaseName = "itineraDB"; // Nome del database

        List<String> collection1Files = List.of(
                "../dataScraping/reviews/hotel_rev/Hotels.json",
                "../dataScraping/reviews/monuments_rev/monuments.json",
                "../dataScraping/reviews/restaurants_rev/restaurants.json",
                "../dataScraping/reviews/museums_rev/museums.json" //aggiunto
        );

        List<String> collection2Files = List.of(
                "../dataScraping/reviews/hotel_rev/ReviewsHotels.json",
                "../dataScraping/reviews/monuments_rev/reviews_monuments.json",
                "../dataScraping/reviews/restaurants_rev/reviews_restaurants.json",
                "../dataScraping/reviews/museums_rev/museums_reviews.json" //aggiunto
        );

        List<String> collection3Files = List.of(
                "../dataScraping/users_data/users.json"
        );

        String collection1Name = "Places";
        String collection2Name = "Reviews";
        String collection3Name = "Users";

        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            System.out.println("Importando dati nella collezione: " + collection1Name);
            importFilesToCollection(database, collection1Name, collection1Files);

            System.out.println("Importando dati nella collezione: " + collection2Name);
            importFilesToCollection(database, collection2Name, collection2Files);

            System.out.println("Importando dati nella collezione: " + collection3Name);
            importFilesToCollection(database, collection3Name, collection3Files);

            System.out.println("Importazione completata per tutte le collezioni.");
        } catch (Exception e) {
            System.err.println("Errore generale durante l'importazione dei dati: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void importFilesToCollection(MongoDatabase database, String collectionName, List<String> filePaths) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        for (String filePath : filePaths) {
            System.out.println("Importando da file: " + filePath);

            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                System.err.println("Il file non esiste o non è leggibile: " + filePath);
                continue;
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Document> documents = objectMapper.readValue(file, new TypeReference<List<Document>>() {});

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
