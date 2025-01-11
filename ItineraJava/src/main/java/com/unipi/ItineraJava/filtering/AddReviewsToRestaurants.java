package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;
// PARTE 6 aggiunta info recensioni a ogni risstorante
public class AddReviewsToRestaurants {

    public static void main(String[] args) {
        // Percorsi dei file
        String restaurantDataFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\restaurants_No_Rev.json";
        String reviewsFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_revFinalFormDue.json";
        String outputFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\restaurants_With_RevDue.json";

        try {
            addReviewsSummaryToRestaurants(restaurantDataFile, reviewsFile, outputFile);
            System.out.println("File aggiornato con successo: " + outputFile);
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiunta del riepilogo delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addReviewsSummaryToRestaurants(String restaurantDataFile, String reviewsFile, String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Carica i dati dei ristoranti
        //crea una lista di mappe chiave-valore
        List<Map<String, Object>> restaurants = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(restaurantDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                restaurants.add(mapper.readValue(line, Map.class));
            }
        }

        // carica le recensioni dal file di input e le mette in una mappa
        List<Map<String, Object>> reviews = mapper.readValue(new File(reviewsFile), List.class);

        // mappa creata a partire dalla precedente, estraggo il campo name e lo uso per indicizzare
        //una lista di mappe che rappresentano le recensioni per quello specifico ristorante
        Map<String, List<Map<String, Object>>> reviewsByRestaurant = new HashMap<>();

        for (Map<String, Object> review : reviews) {
            //prendo campo name
            String restaurantName = (String) review.get("name");
            //metodo per vedere sè è già presente, se non lo è aggiungo alla mappa dei ristoranti una nuova mappa
            //indicizzata dal nome del ristorante e con una lista vuota ad indicare che on ci sono ancora recensioni
            reviewsByRestaurant.putIfAbsent(restaurantName, new ArrayList<>());
            //aggiungo poi normalmente la recesnsione sia che quello fosse il primo ristorante che non
            reviewsByRestaurant.get(restaurantName).add(review);
        }

        // Aggiorna i ristoranti con il riepilogo delle recensioni
        // il for precedente ha creato una mappa <nomeRistorante, lista di recensioni>
        for (Map<String, Object> restaurant : restaurants) {
            String name = (String) restaurant.get("name");
            //usa il metodo get or default per isolare la lista di recensioni del ristorante in questione salvandola in una mappa ulteriore
            // il metodo restituisce il valore di una mappa associato a una chiave, se qeulla chiave non ha un valore associato restituisce il secondo valore, quello di default
            // che deve ovviamenrte essere dello stesso tipo degli oggetti associati alle chiavi nella mappa su cui il metodo è chiamato
            List<Map<String, Object>> restaurantReviews = reviewsByRestaurant.getOrDefault(name, new ArrayList<>());

            // numero totale di recensioni
            int totalReviews = restaurantReviews.size();

            // Calcola la valutazione complessiva
            double totalStars = 0.0;
            for (Map<String, Object> review : restaurantReviews) {
                Object stars = review.get("stars");
                double starValue = 0.0;
                //controllo su tipo di star value
                if (stars instanceof Number) {
                    starValue = ((Number) stars).doubleValue();
                } else {
                    try {
                        starValue = Double.parseDouble(stars.toString());
                    } catch (NumberFormatException e) {
                        System.err.println("Valore non valido per le stelle: " + stars);
                    }
                }
                totalStars += starValue;
            }

            // Calcola la media delle stelle
            double overallRating = totalReviews > 0 ? totalStars / totalReviews : 0.0;

            // Aggiunge il riepilogo come oggetto "reviews_info"
            Map<String, Object> reviewsInfo = new HashMap<>();
            reviewsInfo.put("overall_rating", overallRating);
            reviewsInfo.put("tot_rev_number", totalReviews);

            restaurant.put("reviews_info", reviewsInfo);
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
