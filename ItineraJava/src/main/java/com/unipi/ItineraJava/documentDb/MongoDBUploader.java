package com.unipi.ItineraJava.documentDb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBUploader {

    // Metodo per ottenere una connessione a MongoDB
    public static MongoClient getMongoConnection(String uri) {
        return MongoClients.create(uri);
    }

    public static void main(String[] args) {

        String mongoUri = "mongodb://myUserAdmin:root@localhost:27017/itineraDB?authSource=admin&authMechanism=SCRAM-SHA-1";
        String databaseName = "itineraDB"; // Nome del database
        String collectionName = "Community";   // Nome della collezione
        String jsonFolderPath = "../dataScraping/Community_doc"; // Percorso reale dei file JSON

        // Connessione a MongoDB
        try (MongoClient mongoClient = getMongoConnection(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Leggi i file JSON dalla cartella
            File folder = new File(jsonFolderPath);
            File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("Nessun file JSON trovato nella cartella specificata.");
                return;
            }

            // Caricamento dei file JSON nella collezione
            for (File file : jsonFiles) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    Document document = Document.parse(content);
                    collection.insertOne(document);

                    System.out.println("Documento caricato: " + file.getName());
                } catch (Exception e) {
                    System.err.println("Errore durante il caricamento del file: " + file.getName());
                    e.printStackTrace();
                }
            }

            System.out.println("Tutti i file JSON sono stati caricati nella collezione MongoDB.");
        } catch (Exception e) {
            System.err.println("Errore durante la connessione a MongoDB con l'URI: " + mongoUri);
            e.printStackTrace();
        }
    }
}
