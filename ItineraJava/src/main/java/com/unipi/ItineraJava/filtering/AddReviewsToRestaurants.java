package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

//PARTE 6:
// Aggiunta delle recensioni

public class AddReviewsToRestaurants {

    public static void main(String[] args) {
        // Percorsi dei file
        String restaurantDataFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\restaurants_No_Rev.json";
        String reviewsFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_revFinalForm.json";
        String outputFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\restaurants_With_Rev.json";

        try {
            addReviewsToRestaurants(restaurantDataFile, reviewsFile, outputFile);
            System.out.println("File aggiornato con successo: " + outputFile);
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiunta delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addReviewsToRestaurants(String restaurantDataFile, String reviewsFile, String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Carica i dati dei ristoranti
        List<Map<String, Object>> restaurants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(restaurantDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                restaurants.add(mapper.readValue(line, Map.class));
            }
        }

        // Carica le recensioni
        List<Map<String, Object>> reviews = mapper.readValue(new File(reviewsFile), List.class);

        // Mappa per tracciare le recensioni per ristorante
        Map<String, List<Map<String, Object>>> reviewsByRestaurant = new HashMap<>();
        for (Map<String, Object> review : reviews) {
            String restaurantName = (String) review.get("name");

            // Rimuovi il campo "name" dalle recensioni
            review.remove("name");

            reviewsByRestaurant.putIfAbsent(restaurantName, new ArrayList<>());
            reviewsByRestaurant.get(restaurantName).add(review);
        }


        // Aggiorna i ristoranti con le recensioni
        for (Map<String, Object> restaurant : restaurants) {
            String name = (String) restaurant.get("name");
            List<Map<String, Object>> restaurantReviews = reviewsByRestaurant.getOrDefault(name, new ArrayList<>());

            // Aggiungi le recensioni
            restaurant.put("reviews", restaurantReviews);

            // Calcola il numero totale di recensioni
            int totalReviews = restaurantReviews.size();
            restaurant.put("tot_rev_number", totalReviews);

            // Calcola la valutazione complessiva (overall_rating)
            double overallRating = restaurantReviews.stream()
                    .mapToDouble(review -> {
                        Object stars = review.get("stars");
                        return stars instanceof Number ? ((Number) stars).doubleValue() : Double.parseDouble(stars.toString());
                    })
                    .average()
                    .orElse(0.0);

            restaurant.put("overall_rating", overallRating);
        }

        // Scrivi il file aggiornato
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map<String, Object> restaurant : restaurants) {
                writer.write(mapper.writeValueAsString(restaurant));
                writer.newLine();
            }
        }
    }
}
