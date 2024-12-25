package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

// PARTE 5 GENERO GLI OGGETTI RISTORANTI MA SENZA RECENSIONI ANCORA, PER EVENTUALI MODIFICHE ALL'ARRAY USEREMO QUEL FILE


public class GenerateRestaurants {

    public static void main(String[] args) {
        // Percorsi dei file di input
        String restaurantNamesFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_review_counts_over_50.txt";
        String addressesFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\fakeAddresses.json";
        String outputFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\restaurants_No_Rev.json";


        try {
            generateRestaurantData(restaurantNamesFile, addressesFile, outputFile);
            System.out.println("File JSON generato con successo: " + outputFile);
        } catch (Exception e) {
            System.err.println("Errore durante la generazione del file JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void generateRestaurantData(String restaurantNamesFile, String addressesFile, String outputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Carica i nomi dei ristoranti dal file di testo
        List<String> restaurantNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(restaurantNamesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0) {
                    restaurantNames.add(parts[0].trim());
                }
            }
        }

        // Carica gli indirizzi dal file JSON
        List<Map<String, Object>> addresses = mapper.readValue(new File(addressesFile), List.class);

        // Assicurati che il numero di ristoranti corrisponda al numero di indirizzi
        if (restaurantNames.size() != addresses.size()) {
            throw new IllegalStateException("Il numero di ristoranti non corrisponde al numero di indirizzi.");
        }

        // Scrivi il file JSON con gli oggetti separati
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (int i = 0; i < restaurantNames.size(); i++) {
                Map<String, Object> address = addresses.get(i);
                Map<String, Object> restaurantObject = new LinkedHashMap<>();

                restaurantObject.put("id", String.format("%05d", i)); // ID a 5 cifre
                restaurantObject.put("name", restaurantNames.get(i));
                restaurantObject.put("address", address.get("address"));
                restaurantObject.put("city", address.get("city"));
                restaurantObject.put("category", "restaurant");
                restaurantObject.put("reviews", new ArrayList<>()); // Array di oggetti vuoto

                // Scrivi ogni oggetto JSON su una nuova riga
                writer.write(mapper.writeValueAsString(restaurantObject));
                writer.newLine();
            }
        }
    }
}
