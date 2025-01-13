package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;


//PARTE 3:
//un sacco di tempo per eseguire, 15/20 secondi
// per ognuno dei 1221 ristoranti scorre il file delle recensioni finchè non ne trova 50, poi passa al successivo


public class rev_NY_endFilter {

    public static void main(String[] args) {
        // Percorso del file JSON di origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_reduced.json";
        // Percorso del file TXT contenente i nomi dei ristoranti
        String restaurantsFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_review_counts_over_50.txt";
        // Percorso del file JSON di destinazione
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_50revPerRest.json";

        try {
            filterReviewsSimple(inputFilePath, restaurantsFilePath, outputFilePath);
            System.out.println("Recensioni filtrate salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante il filtraggio delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void filterReviewsSimple(String inputFilePath, String restaurantsFilePath, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Leggi il file TXT per ottenere i nomi dei ristoranti
        List<String> restaurantNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(restaurantsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0) {
                    restaurantNames.add(parts[0].trim());
                }
            }
        }

        // Leggi tutte le recensioni dal file JSON
        List<Map<String, Object>> allReviews = mapper.readValue(new File(inputFilePath), List.class);

        // Lista per memorizzare le recensioni filtrate
        List<Map<String, Object>> filteredReviews = new ArrayList<>();

        // Filtra le prime 50 recensioni per ogni ristorante nella lista
        for (String restaurantName : restaurantNames) {
            int count = 0;
            for (Map<String, Object> review : allReviews) {
                if (restaurantName.equals(review.get("name"))) {
                    filteredReviews.add(review);
                    count++;
                    if (count == 50) {
                        break;
                    }
                }
            }
        }

        // Scrivi le recensioni filtrate nel file di destinazione
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, filteredReviews);
        }
    }
}
