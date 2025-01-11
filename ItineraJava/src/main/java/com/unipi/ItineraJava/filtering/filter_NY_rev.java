package com.unipi.ItineraJava.filtering;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


//PARTE 1:
//eliminati campi non necessari da file json per ridurne la dimensione

public class filter_NY_rev {

    public static void main(String[] args) {
        // Percorso del file JSON di origine con ogni singolo campo estratto da kaggle   QUESTO FILE NON è NELLA CARTELLA, SPOSTATO PER FACILITARE LA PUSH CON MEZZO GIGA IN MENO
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_2.json";
        // Percorso del file JSON di destinazione con i campi filtrati e rinominati correttamente
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_reduced.json";

        try {
            modifyReviews(inputFilePath, outputFilePath);
            System.out.println("Recensioni modificate salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante la modifica delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void modifyReviews(String inputFilePath, String outputFilePath) throws IOException {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Array per memorizzare le recensioni modificate
        List<JSONObject> modifiedReviews = new ArrayList<>();

        try (JsonParser parser = factory.createParser(new File(inputFilePath))) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Il file JSON deve iniziare con un array.");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                // Leggi l'oggetto JSON corrente
                JSONObject review = mapper.readValue(parser, JSONObject.class);

                // Crea un nuovo oggetto JSON con i campi desiderati
                JSONObject modifiedReview = new JSONObject();
                if (review.containsKey("restaurant_name")) {
                    modifiedReview.put("name", review.get("restaurant_name"));
                }
                if (review.containsKey("rating_review")) {
                    modifiedReview.put("rating", review.get("rating_review"));
                }
                if (review.containsKey("review_id")) {
                    modifiedReview.put("review_id", review.get("review_id"));
                }
                if (review.containsKey("review_full")) {
                    modifiedReview.put("text", review.get("review_full"));
                }
                if (review.containsKey("date")) {
                    modifiedReview.put("date", review.get("date"));
                }
                if (review.containsKey("author_id")) {
                    modifiedReview.put("author_id", review.get("author_id"));
                }

                // Aggiungi l'oggetto modificato alla lista
                modifiedReviews.add(modifiedReview);
            }
        }

        // Scrivi il risultato nel file JSON di destinazione
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            mapper.writeValue(writer, modifiedReviews);
        }
    }
}
