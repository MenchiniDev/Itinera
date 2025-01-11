package com.unipi.ItineraJava.filtering;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommunityBuilder {

    private static final AtomicInteger globalIdCounter = new AtomicInteger(50465); // Contatore globale per ID
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Percorsi dei file
        String postsFolderPath = "/Users/rossana/LargeScale/itinera/dataScraping/Post_doc"; // Cartella con i JSON dei post
        String outputFolderPath = "/Users/rossana/LargeScale/itinera/dataScraping/Community_doc"; // Cartella di output per i file delle community

        File folder = new File(postsFolderPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.matches("file\\d+_.+\\.json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("Nessun file JSON trovato nella cartella specificata.");
            return;
        }

        // Creare la cartella di output se non esiste
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
            System.out.println("Cartella di output creata: " + outputFolderPath);
        }

        // Mappa per garantire una sola community per città
        Map<String, JSONObject> communityMap = new HashMap<>();

        for (File file : jsonFiles) {
            try {
                JSONParser parser = new JSONParser();
                FileReader reader = new FileReader(file);
                JSONObject postJson = (JSONObject) parser.parse(reader);
                reader.close();

                // Estrai il nome della città e della community dal nome del file
                String fileName = file.getName();
                String communityName = extractCommunityNameFromFileName(fileName);
                String cityName = getCityNameFromCommunityName(communityName);

                // Crea il documento della community se non esiste
                communityMap.putIfAbsent(communityName, createCommunityDocument(cityName, communityName));

                // Aggiungi i post al documento della community (massimo 2 per community)
                JSONObject community = communityMap.get(communityName);
                JSONArray postsArray = (JSONArray) community.get("Post");

                // Trova il timestamp minimo tra i post
                String postTimestamp = (String) postJson.get("Timestamp");
                String currentMinTimestamp = (String) community.get("Created");
                if (compareTimestamps(postTimestamp, currentMinTimestamp) < 0) {
                    community.put("Created", postTimestamp); // Aggiorna il timestamp minimo
                }

                if (postsArray.size() < 2) {
                    JSONObject post = createPostDocument(postJson);
                    postsArray.add(post);
                }

            } catch (IOException | ParseException e) {
                System.err.println("Errore durante l'elaborazione del file: " + file.getName());
                e.printStackTrace();
            }
        }

        // Salva ogni community in un file separato
        saveCommunityDocuments(communityMap, outputFolderPath);

        // Stampare l'ultimo ID generato
        System.out.println("L'ultimo ID utilizzato è: " + (globalIdCounter.get() - 1));
    }

    /**
     * Estrae il nome della community dal nome del file.
     */
    private static String extractCommunityNameFromFileName(String fileName) {
        try {
            String communityPart = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
            return communityPart.substring(0, 1).toUpperCase() + communityPart.substring(1); // Prima lettera maiuscola
        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione del nome della community dal file: " + fileName);
            return "Unknown";
        }
    }

    /**
     * Determina il nome della città basandosi sul nome della community.
     */
    private static String getCityNameFromCommunityName(String communityName) {
        if (communityName.equals("FCInterMilan")) {
            return "Milan";
        }
        return communityName; // Default: il nome della città è uguale al nome della community
    }

    /**
     * Crea un documento per una community.
     */
    private static JSONObject createCommunityDocument(String cityName, String communityName) {
        JSONObject community = new JSONObject();
        community.put("Id", globalIdCounter.getAndIncrement()); // Aggiunta ID univoco
        community.put("City", cityName);
        community.put("Name", communityName);
        community.put("Created", "9999-12-31 23:59:59"); // Timestamp iniziale massimo
        community.put("Post", new JSONArray()); // Array vuoto per i post (sarà posizionato per ultimo)
        return community;
    }

    /**
     * Crea un documento per un post.
     */
    private static JSONObject createPostDocument(JSONObject postJson) {
        JSONObject post = new JSONObject();
        post.put("User", postJson.get("Username"));
        post.put("Text", postJson.get("Post_body"));
        post.put("Timestamp", postJson.get("Timestamp"));
        return post;
    }

    /**
     * Confronta due timestamp formattati come "yyyy-MM-dd HH:mm:ss".
     */
    private static int compareTimestamps(String timestamp1, String timestamp2) {
        try {
            LocalDateTime time1 = LocalDateTime.parse(timestamp1, TIMESTAMP_FORMATTER);
            LocalDateTime time2 = LocalDateTime.parse(timestamp2, TIMESTAMP_FORMATTER);
            return time1.compareTo(time2);
        } catch (Exception e) {
            System.err.println("Errore durante il confronto dei timestamp: " + timestamp1 + " e " + timestamp2);
            return 0;
        }
    }

    /**
     * Salva ogni documento della community in un file JSON separato.
     */
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
