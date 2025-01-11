package com.unipi.ItineraJava.filtering;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBUploader {

    public static void main(String[] args) {
        // Configurazione MongoDB
        String mongoUri = "mongodb://myUserAdmin:root@localhost:27017"; // URI di connessione
        String databaseName = "itineraDB";            // Nome del database
        String collectionName = "Community";             // Nome della collezione

        // Cartella contenente i file JSON
        String jsonFolderPath = "/Users/rossana/LargeScale/itinera/dataScraping/Community_doc"; // Percorso reale

        // Connessione a MongoDB
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
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
                    // Leggi il contenuto del file
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

                    // Converti il contenuto in un documento BSON
                    Document document = Document.parse(content);

                    // Inserisci il documento nella collezione
                    collection.insertOne(document);

                    System.out.println("Documento caricato: " + file.getName());
                } catch (Exception e) {
                    System.err.println("Errore durante il caricamento del file: " + file.getName());
                    e.printStackTrace();
                }
            }

            System.out.println("Tutti i file JSON sono stati caricati nella collezione MongoDB.");
        } catch (Exception e) {
            System.err.println("Errore durante la connessione a MongoDB.");
            e.printStackTrace();
        }
    }
}
