package com.unipi.ItineraJava.filtering;
// file per pulizia dati kaggle ristoranti NewYork
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FilterReviews {

    public static void main(String[] args) {
        // Percorso del file JSON di origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews.json";
        // Percorso del file JSON di destinazione
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\filteredReviews.json";

        try {
            filterReviews(inputFilePath, outputFilePath);
            System.out.println("Recensioni filtrate salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante il filtraggio delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void filterReviews(String inputFilePath, String outputFilePath) throws IOException, ParseException {
        // Leggi il file JSON di origine
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(inputFilePath);
        JSONArray reviewsArray = (JSONArray) parser.parse(reader);
        reader.close();

        // Lista per tracciare i primi 40 ristoranti
        List<String> restaurantList = new ArrayList<>();
        // Lista parallela per tracciare il conteggio delle recensioni
        List<Integer> reviewCounts = new ArrayList<>();

        // Array per memorizzare le recensioni filtrate
        JSONArray filteredReviews = new JSONArray();

        for (Object obj : reviewsArray) {
            if (obj instanceof JSONObject) {
                JSONObject review = (JSONObject) obj;

                // Controlla il nome del ristorante
                String restaurantName = (String) review.get("name");
                if (restaurantName == null) {
                    continue; // Salta se il nome del ristorante non è disponibile
                }

                // Verifica se il ristorante è già nella lista
                int index = restaurantList.indexOf(restaurantName);

                if (index == -1) { // Ristorante non ancora aggiunto
                    if (restaurantList.size() < 40) {
                        restaurantList.add(restaurantName);
                        reviewCounts.add(1); // Aggiungi il conteggio della prima recensione
                        filteredReviews.add(review);
                    }
                } else { // Ristorante già nella lista
                    int currentCount = reviewCounts.get(index);
                    if (currentCount < 35) {
                        filteredReviews.add(review);
                        reviewCounts.set(index, currentCount + 1);
                    }
                }
            }
        }

        // Scrivi le recensioni filtrate nel file di destinazione
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        FileWriter writer = new FileWriter(outputFilePath);
        objectMapper.writeValue(writer, filteredReviews);
        writer.close();
    }
}
