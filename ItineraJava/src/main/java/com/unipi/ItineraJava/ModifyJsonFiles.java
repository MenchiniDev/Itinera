package com.unipi.ItineraJava;

import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ModifyJsonFiles {

    public static void main(String[] args) {
        // Percorso assoluto della cartella
        String folderPath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\places";

        // Trova tutti i file JSON nella cartella "places"
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
            Object parsed = parser.parse(reader);
            reader.close();

            if (parsed instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) parsed;

                // Crea un nuovo array con solo i campi desiderati
                JSONArray modifiedArray = new JSONArray();

                for (Object obj : jsonArray) {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) obj;

                        JSONObject modifiedJson = new JSONObject();
                        if (jsonObject.containsKey("name")) {
                            modifiedJson.put("name", jsonObject.get("name"));
                        }
                        if (jsonObject.containsKey("place_id")) {
                            modifiedJson.put("place_id", jsonObject.get("place_id"));
                        }
                        if (jsonObject.containsKey("rating")) {
                            modifiedJson.put("rating", jsonObject.get("rating"));
                        }
                        if (jsonObject.containsKey("reference")) {
                            modifiedJson.put("reference", jsonObject.get("reference"));
                        }
                        if (jsonObject.containsKey("vicinity")) {
                            modifiedJson.put("vicinity", jsonObject.get("vicinity"));
                        }
                        if (jsonObject.containsKey("user_ratings_total")) {
                            modifiedJson.put("user_ratings_total", jsonObject.get("user_ratings_total"));
                        }
                        modifiedArray.add(modifiedJson);
                    }
                }

                // Usa Jackson per scrivere il JSON formattato
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Attiva il pretty-print
                FileWriter writer = new FileWriter(jsonFile);
                objectMapper.writeValue(writer, modifiedArray);
                writer.close();

                System.out.println("Modificato con successo (formattato): " + jsonFile.getName());
            } else {
                System.err.println("Il file " + jsonFile.getName() + " non contiene un array di oggetti JSON.");
            }

        } catch (IOException | ParseException e) {
            System.err.println("Errore durante la modifica del file: " + jsonFile.getName());
            e.printStackTrace();
        }
    }
}
