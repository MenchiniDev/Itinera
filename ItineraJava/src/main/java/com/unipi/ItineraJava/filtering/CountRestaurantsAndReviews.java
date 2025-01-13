package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// PARTE 2:
// codice fatto per trovare tutti quei ristoranti con più di 50 recensioni e scriverlo in un file di testo


public class CountRestaurantsAndReviews {

    public static void main(String[] args) {
        // Percorso del file JSON di origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_reduced.json";
        // Percorso del file di destinazione per salvare i risultati
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_review_counts_over_50.txt";

        try {
            countRestaurantsWithMoreThan50Reviews(inputFilePath, outputFilePath);
            System.out.println("Risultati salvati con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante il conteggio dei ristoranti e delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void countRestaurantsWithMoreThan50Reviews(String inputFilePath, String outputFilePath) throws IOException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(); // Aggiunto ObjectMapper per la deserializzazione

        // Mappa per tracciare il numero di recensioni per ogni ristorante
        Map<String, Integer> restaurantReviewCounts = new HashMap<>();

        try (JsonParser parser = factory.createParser(new File(inputFilePath))) {
            parser.setCodec(mapper); // Associa l'ObjectMapper al parser

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Il file JSON deve iniziare con un array.");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Leggi l'oggetto JSON corrente
                Map<String, Object> review = parser.readValueAs(Map.class);

                // Recupera il nome del ristorante
                String restaurantName = (String) review.get("name");
                if (restaurantName == null) {
                    continue; // Salta se il nome del ristorante non è disponibile
                }

                // Incrementa il conteggio delle recensioni per il ristorante
                restaurantReviewCounts.put(restaurantName, restaurantReviewCounts.getOrDefault(restaurantName, 0) + 1);
            }
        }

        // Filtra i ristoranti con più di 50 recensioni e scrivi i risultati nel file di destinazione
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("Numero totale di ristoranti con più di 50 recensioni: " + restaurantReviewCounts.values().stream().filter(count -> count > 49).count());
            writer.newLine();
            writer.write("Ristoranti con più di 50 recensioni:");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : restaurantReviewCounts.entrySet()) {
                if (entry.getValue() > 49) {
                    writer.write(entry.getKey() + ": " + entry.getValue() + " recensioni");
                    writer.newLine();
                }
            }
        }
    }
}
