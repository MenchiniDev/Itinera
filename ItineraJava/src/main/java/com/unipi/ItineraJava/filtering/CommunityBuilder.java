package com.unipi.ItineraJava.filtering;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommunityBuilder {

    private static final AtomicInteger globalIdCounter = new AtomicInteger(200436); // Contatore globale per ID
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Percorsi dei file
        String postsFolderPath = "itinera/dataScraping/Post_doc"; // Cartella con i JSON dei post
        String outputFolderPath = "itinera/dataScraping/Community_doc"; // Cartella di output per i file delle community

        File folder = new File(postsFolderPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.matches("file\\d+_.+\\.json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("Nessun file JSON trovato nella cartella specificata.");
            return;
        }

        // Crea la cartella di output se non esiste
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
            System.out.println("Cartella di output creata: " + outputFolderPath);
        }

        // Mappa per garantire una sola community per città
        Map<String, List<JSONObject>> communityPostsMap = new HashMap<>();

        for (File file : jsonFiles) {
            try {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject postJson = (JSONObject) parser.parse(reader);
                reader.close();

                // Estrae il nome della città e della community dal nome del file
                String fileName = file.getName();
                String communityName = extractCommunityNameFromFileName(fileName);
                String cityName = getCityNameFromCommunityName(communityName);

                // Inizializza la lista di post per la community se non esiste
                communityPostsMap.putIfAbsent(communityName, new ArrayList<>());

                // Corregge il timestamp del post
                postJson.put("Timestamp", correctTimestamp((String) postJson.get("Timestamp")));

                // Aggiunge il post alla lista della community
                communityPostsMap.get(communityName).add(postJson);

            } catch (IOException | ParseException e) {
                System.err.println("Errore durante l'elaborazione del file: " + file.getName());
                e.printStackTrace();
            }
        }

        // Creazione dei documenti delle community
        Map<String, JSONObject> communityMap = new HashMap<>();

        for (Map.Entry<String, List<JSONObject>> entry : communityPostsMap.entrySet()) {
            String communityName = entry.getKey();
            List<JSONObject> posts = entry.getValue();

            // Ordina i post per timestamp
            posts.sort(Comparator.comparing(post -> LocalDateTime.parse((String) post.get("Timestamp"), TIMESTAMP_FORMATTER)));

            // Crea il documento della community
            String cityName = getCityNameFromCommunityName(communityName);
            JSONObject community = createCommunityDocument(cityName, communityName);

            // Aggiunge i due post più vecchi alla community
            JSONArray postsArray = (JSONArray) community.get("Post");
            for (int i = 0; i < Math.min(2, posts.size()); i++) {
                JSONObject post = createPostDocument(posts.get(i));
                postsArray.add(post);
            }

            // Aggiorna il timestamp "Created" con il timestamp del post più vecchio meno un'ora
            if (!posts.isEmpty()) {
                String oldestTimestamp = (String) posts.get(0).get("Timestamp");
                String adjustedTimestamp = subtractOneHour(oldestTimestamp);
                community.put("Created", adjustedTimestamp);
            }


            communityMap.put(communityName, community);
        }

        // Salva ogni community in un file separato
        saveCommunityDocuments(communityMap, outputFolderPath);

        // Stampa l'ultimo ID generato
        System.out.println("L'ultimo ID utilizzato è: " + (globalIdCounter.get() - 1));
    }

    private static String subtractOneHour(String timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER);
            // Sottrae un'ora
            dateTime = dateTime.minusHours(1);
            return dateTime.format(TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            System.err.println("Errore durante la sottrazione di un'ora dal timestamp: " + timestamp);
            return timestamp; // Ritorna il valore originale in caso di errore
        }
    }
    

    private static String extractCommunityNameFromFileName(String fileName) {
        try {
            String communityPart = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
            return communityPart.substring(0, 1).toUpperCase() + communityPart.substring(1); // Prima lettera maiuscola
        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione del nome della community dal file: " + fileName);
            return "Unknown";
        }
    }

    private static String getCityNameFromCommunityName(String communityName) {
        return communityName;
    }

    private static JSONObject createCommunityDocument(String cityName, String communityName) {
        JSONObject community = new JSONObject();
        community.put("Id", globalIdCounter.getAndIncrement());
        community.put("City", cityName);
        community.put("Name", communityName);
        community.put("Created", "9999-12-31 23:59:59");
        community.put("Post", new JSONArray());
        return community;
    }

    private static JSONObject createPostDocument(JSONObject postJson) {
        JSONObject post = new JSONObject();
        post.put("User", postJson.get("Username"));
        post.put("Text", postJson.get("Post_body"));
        post.put("Timestamp", postJson.get("Timestamp"));
        return post;
    }

    private static String correctTimestamp(String timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER);

            // Corregge l'anno se fuori dal range accettabile
            if (dateTime.getYear() > 2050 || dateTime.getYear() < 1970) {
                dateTime = dateTime.withYear(2024);
            }

            return dateTime.format(TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            System.err.println("Timestamp non valido: " + timestamp + ". Corretto al valore di default.");
            return "2024-01-01 00:00:00";
        }
    }

    private static void saveCommunityDocuments(Map<String, JSONObject> communityMap, String outputFolderPath) {
        for (Map.Entry<String, JSONObject> entry : communityMap.entrySet()) {
            String communityName = entry.getKey();
            JSONObject communityDocument = entry.getValue();

            // Riposiziona l'attributo "Post" come ultimo elemento
            LinkedHashMap<String, Object> orderedCommunity = new LinkedHashMap<>();
            orderedCommunity.put("Id", communityDocument.get("Id"));
            orderedCommunity.put("City", communityDocument.get("City"));
            orderedCommunity.put("Name", communityDocument.get("Name"));
            orderedCommunity.put("Created", communityDocument.get("Created"));
            orderedCommunity.put("Post", communityDocument.get("Post"));

            String outputFilePath = outputFolderPath + File.separator + communityName + ".json";
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.writeValue(writer, orderedCommunity);
                System.out.println("File salvato: " + outputFilePath);
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio del file per la community: " + communityName);
                e.printStackTrace();
            }
        }
    }
}
