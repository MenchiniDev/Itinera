package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONObject;

// codice inulie
//IL FILE DI RIFERIMENTO DI QUESTO CODICE PESA TROPPO POCO, ERA UN CODICE DI ESPERIMENTO

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilterReviews {

    public static void main(String[] args) {
        // Percorso del file JSON di origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_reduced.json";
        // Percorso del file JSON di destinazione
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_filtered.json";

        try {
            filterReviews(inputFilePath, outputFilePath);
            System.out.println("Recensioni filtrate salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante il filtraggio delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void filterReviews(String inputFilePath, String outputFilePath) throws IOException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Lista per tracciare i primi 40 ristoranti
        List<String> restaurantList = new ArrayList<>();
        List<Integer> reviewCounts = new ArrayList<>();

        // Array per memorizzare le recensioni filtrate
        List<JSONObject> filteredReviews = new ArrayList<>();

        try (JsonParser parser = factory.createParser(new File(inputFilePath))) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Il file JSON deve iniziare con un array.");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Interrompi se il limite di 40 ristoranti è stato raggiunto e tutti hanno 35 recensioni
                if (restaurantList.size() >= 50 && reviewCounts.stream().allMatch(count -> count >= 50)) {
                    break;
                }

                // Leggi l'oggetto JSON corrente
                JSONObject review = mapper.readValue(parser, JSONObject.class);

                // Escludi oggetti con valori NaN
                if (review.values().contains(Double.NaN)) {
                    continue;
                }

                // Controlla il nome del ristorante
                String restaurantName = (String) review.get("name");
                if (restaurantName == null) {
                    continue;
                }

                // Verifica se il ristorante è già nella lista
                int index = restaurantList.indexOf(restaurantName);

                if (index == -1) { // Ristorante non ancora aggiunto
                    if (restaurantList.size() < 50) {
                        restaurantList.add(restaurantName);
                        reviewCounts.add(1);
                        filteredReviews.add(review);
                    }
                } else { // Ristorante già nella lista
                    int currentCount = reviewCounts.get(index);
                    if (currentCount < 50) {
                        filteredReviews.add(review);
                        reviewCounts.set(index, currentCount + 1);
                    }
                }
            }
        }

        // Scrivi il risultato nel file JSON di destinazione
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            mapper.writeValue(writer, filteredReviews);
        }
    }
}
