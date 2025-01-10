package com.unipi.ItineraJava.filtering;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PostsCleaning {

    // Mappa per tenere traccia dei contatori per ciascuna community
    private static final Map<String, Integer> communityFileCounter = new HashMap<>();
    private static final Set<String> uniqueUsernames = new HashSet<>(); // Set per gli username univoci

    public static void main(String[] args) {
        // Percorso assoluto della cartella con i file JSON originali
        String sourceFolderPath = "/Users/rossana/LargeScale/itinera/dataScraping/posts2";
        String outputFolderPath = sourceFolderPath + File.separator + "../transformed";
        String usernamesFilePath = outputFolderPath + File.separator + "../usernames.txt";

        // Creare la cartella di output se non esiste
        File outputFolder = new File(outputFolderPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
            System.out.println("Cartella di output creata: " + outputFolderPath);
        }

        File folder = new File(sourceFolderPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (jsonFiles != null) {
            for (File file : jsonFiles) {
                try {
                    transformJsonFile(file, outputFolderPath);
                } catch (Exception e) {
                    System.err.println("Errore durante la modifica del file: " + file.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("La cartella specificata non contiene file JSON.");
        }

        // Scrivere gli username univoci nel file usernames.txt
        saveUsernamesToFile(usernamesFilePath);
    }

    private static void transformJsonFile(File jsonFile, String outputFolderPath) {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(jsonFile);
            JSONObject originalJson = (JSONObject) parser.parse(reader);
            reader.close();
    
            // Usare LinkedHashMap per mantenere l'ordine degli attributi
            LinkedHashMap<String, Object> transformedJson = new LinkedHashMap<>();
    
            // 📝 Estrazione dei campi principali
            String communityName = extractCommunityNameFromFileName(jsonFile.getName()); // Estratto dal nome file
            String user = (String) originalJson.get("post_author");
            String postBody = (String) originalJson.get("post_title"); // Rinominato post_title in post_body
    
            // Aggiungere username del post al set
            if (user != null) {
                uniqueUsernames.add(user);
            }
    
            // 🕒 Gestione timestamp
            long timestamp = ((JSONArray) originalJson.get("comments")).stream()
                    .filter(comment -> comment instanceof JSONObject)
                    .mapToLong(comment -> {
                        JSONObject jsonComment = (JSONObject) comment;
                        Object time = jsonComment.get("created_utc");
                        if (time instanceof Number) {
                            return ((Number) time).longValue();
                        }
                        return System.currentTimeMillis();
                    })
                    .min()
                    .orElse(System.currentTimeMillis());
    
            String formattedTimestamp = convertTimestamp(timestamp);
            int numComments = ((JSONArray) originalJson.get("comments")).size();
    
            // 📝 Aggiunta dei campi principali nell'ordine desiderato
            transformedJson.put("Community_name", communityName);
            transformedJson.put("Username", user);
            transformedJson.put("Post_body", postBody); // Rinominato
            transformedJson.put("Timestamp", formattedTimestamp);
            transformedJson.put("Num_comment", numComments);
            transformedJson.put("reported", false);
    
            // 📝 Trasformazione dei commenti
            JSONArray transformedComments = new JSONArray();
            JSONArray originalComments = (JSONArray) originalJson.get("comments");
    
            for (Object obj : originalComments) {
                if (obj instanceof JSONObject) {
                    JSONObject originalComment = (JSONObject) obj;
                    JSONObject transformedComment = new JSONObject();
    
                    Object commentTime = originalComment.get("created_utc");
                    long commentTimestamp = (commentTime instanceof Number) ? ((Number) commentTime).longValue() : System.currentTimeMillis();
    
                    String commentUser = (String) originalComment.get("author");
                    if (commentUser != null) {
                        uniqueUsernames.add(commentUser); // Aggiungere username del commento
                    }
    
                    transformedComment.put("Username", commentUser);
                    transformedComment.put("Timestamp", convertTimestamp(commentTimestamp));
                    transformedComment.put("Body", originalComment.get("body"));
                    transformedComment.put("reported", false);
    
                    transformedComments.add(transformedComment);
                }
            }
    
            // 📝 Aggiunta dei commenti come ultimo attributo
            transformedJson.put("Commenti", transformedComments);
    
            // 📁 Naming dei file di output
            int counter = communityFileCounter.getOrDefault(communityName, 0) + 1;
            communityFileCounter.put(communityName, counter);
    
            String outputFileName = String.format("file%d_%s.json", counter, communityName);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    
            String outputFilePath = outputFolderPath + File.separator + outputFileName;
            FileWriter writer = new FileWriter(outputFilePath);
            objectMapper.writeValue(writer, transformedJson);
            writer.close();
    
            System.out.println("File trasformato con successo: " + jsonFile.getName() + " → " + outputFilePath);
    
        } catch (IOException | ParseException e) {
            System.err.println("Errore durante la modifica del file: " + jsonFile.getName());
            e.printStackTrace();
        }
    }
    

   private static void saveUsernamesToFile(String filePath) {
    File usernamesFile = new File(filePath);
    if (usernamesFile.exists()) {
        System.out.println("Il file usernames.txt esiste già. Non verrà sovrascritto.");
        return; // Evita di sovrascrivere il file
    }

    try (FileWriter writer = new FileWriter(usernamesFile)) {
        for (String username : uniqueUsernames.stream().sorted().collect(Collectors.toList())) {
            writer.write(username + "\n");
        }
        System.out.println("File degli username salvato con successo: " + filePath);
    } catch (IOException e) {
        System.err.println("Errore durante il salvataggio del file usernames.txt");
        e.printStackTrace();
    }
}

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    

    private static String extractCommunityNameFromFileName(String fileName) {
        try {
            // Dividi il nome del file con "_" e prendi il secondo elemento (indice 1)
            String[] parts = fileName.split("_");
            if (parts.length > 2) {
                return capitalizeFirstLetter(parts[2]); // Nome della community con la prima lettera maiuscola
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione della community dal nome file: " + fileName);
        }
        return "Unknown"; // Restituisci "Unknown" come fallback
    }
    
    

    private static String convertTimestamp(long timestamp) {
        try {
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            System.err.println("Errore durante la conversione del timestamp: " + timestamp);
            return "Invalid Timestamp";
        }
    }
}
