package com.unipi.ItineraJava.filtering;

import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ModifyReviews {

    public static void main(String[] args) {
        // Percorso assoluto della cartella contenente i file JSON
        String folderPath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews";

        // Trova tutti i file JSON nella cartella
        File folder = new File(folderPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (jsonFiles != null) {
            for (File file : jsonFiles) {
                try {
                    // Modifica il file JSON
                    modifyJsonFile(file);
                } catch (Exception e) {
                    System.err.println("Errore durante la modifica del file: " + file.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("La cartella specificata non contiene file JSON.");
        }
    }

    private static void modifyJsonFile(File jsonFile) {
        try {
            // Leggi il file JSON
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(jsonFile);
            JSONObject hotel = (JSONObject) parser.parse(reader);
            reader.close();

            // Ottieni l'array "reviews" e modifica i suoi oggetti
            if (hotel.containsKey("reviews")) {
                JSONArray reviews = (JSONArray) hotel.get("reviews");
                JSONArray modifiedReviews = new JSONArray();

                for (Object obj : reviews) {
                    if (obj instanceof JSONObject) {
                        JSONObject review = (JSONObject) obj;
                        JSONObject modifiedReview = new JSONObject();

                        // Conserva solo i campi desiderati
                        if (review.containsKey("author_name")) {
                            modifiedReview.put("author_name", review.get("author_name"));
                        }
                        if (review.containsKey("rating")) {
                            modifiedReview.put("rating", review.get("rating"));
                        }
                        if (review.containsKey("text")) {
                            modifiedReview.put("text", review.get("text"));
                        }
                        if (review.containsKey("time")) {
                            modifiedReview.put("time", review.get("time"));
                        }

                        // Aggiungi il nuovo campo "reported" con valore false
                        modifiedReview.put("reported", false);

                        modifiedReviews.add(modifiedReview);
                    }
                }

                // Aggiorna l'array "reviews" con la versione modificata
                hotel.put("reviews", modifiedReviews);
            }

            // Salva il file JSON indentati bene
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            FileWriter writer = new FileWriter(jsonFile);
            objectMapper.writeValue(writer, hotel);
            writer.close();

            System.out.println("Modificato con successo (formattato): " + jsonFile.getName());

        } catch (IOException | ParseException e) {
            System.err.println("Errore durante la modifica del file: " + jsonFile.getName());
            e.printStackTrace();
        }
    }
}
