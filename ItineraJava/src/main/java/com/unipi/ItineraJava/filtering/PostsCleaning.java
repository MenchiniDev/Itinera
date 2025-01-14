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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PostsCleaning {

    private static final Map<String, Integer> communityFileCounter = new HashMap<>();
    private static final Set<String> uniqueUsernames = new HashSet<>(); 
    private static final AtomicInteger globalIdCounter = new AtomicInteger(200000); 

    private static final Set<String> ignoredPatterns = Set.of("england", "FCInterMilan", "lombardia");

    public static void main(String[] args) {
        String sourceFolderPath = "itinera/dataScraping/posts2";
        String outputFolderPath = sourceFolderPath + File.separator + "../Post_doc";
        String usernamesFilePath = outputFolderPath + File.separator + "../usernames.txt";

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

        saveUsernamesToFile(usernamesFilePath);

        System.out.println("L'ultimo ID utilizzato è: " + (globalIdCounter.get() - 1));
    }

    private static void transformJsonFile(File jsonFile, String outputFolderPath) {
        String fileName = jsonFile.getName();

        if (ignoredPatterns.stream().anyMatch(fileName::contains)) {
            System.out.println("File ignorato: " + fileName);
            return;
        }

        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(jsonFile);
            JSONObject originalJson = (JSONObject) parser.parse(reader);
            reader.close();

            LinkedHashMap<String, Object> transformedJson = new LinkedHashMap<>();

            String communityName = extractCommunityNameFromFileName(fileName);
            String user = (String) originalJson.get("post_author");
            String postBody = (String) originalJson.get("post_title");

            if (user != null) {
                uniqueUsernames.add(user);
            }

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

            String formattedTimestamp = convertAndCorrectTimestamp(timestamp);
            int numComments = ((JSONArray) originalJson.get("comments")).size();

            transformedJson.put("Id", globalIdCounter.getAndIncrement());
            transformedJson.put("Community_name", communityName);
            transformedJson.put("Username", user);
            transformedJson.put("Post_body", postBody);
            transformedJson.put("Timestamp", formattedTimestamp);
            transformedJson.put("Num_comment", numComments);
            transformedJson.put("reported_post", false);

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
                        uniqueUsernames.add(commentUser);
                    }

                    transformedComment.put("Username", commentUser);
                    transformedComment.put("Timestamp", convertAndCorrectTimestamp(commentTimestamp));
                    transformedComment.put("Body", originalComment.get("body"));
                    transformedComment.put("reported_comment", false);

                    transformedComments.add(transformedComment);
                }
            }

            transformedJson.put("Commenti", transformedComments);

            int counter = communityFileCounter.getOrDefault(communityName, 0) + 1;
            communityFileCounter.put(communityName, counter);

            String outputFileName = String.format("file%d_%s.json", counter, communityName);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String outputFilePath = outputFolderPath + File.separator + outputFileName;
            FileWriter writer = new FileWriter(outputFilePath);
            objectMapper.writeValue(writer, transformedJson);
            writer.close();

            System.out.println("File trasformato con successo: " + fileName + " → " + outputFilePath);

        } catch (IOException | ParseException e) {
            System.err.println("Errore durante la modifica del file: " + jsonFile.getName());
            e.printStackTrace();
        }
    }

    private static void saveUsernamesToFile(String filePath) {
        File usernamesFile = new File(filePath);
        if (usernamesFile.exists()) {
            System.out.println("Il file usernames.txt esiste già. Non verrà sovrascritto.");
            return;
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
            String[] parts = fileName.split("_");
            if (parts.length > 2) {
                return capitalizeFirstLetter(parts[2]);
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione della community dal nome file: " + fileName);
        }
        return "Unknown";
    }

    private static String convertAndCorrectTimestamp(long timestamp) {
        try {
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            // Corregge l'anno se è errato
            if (dateTime.getYear() < 2018) {
                dateTime = dateTime.withYear(2022); // Anni sotto il 2018 vengono corretti al 2022
            } else if (dateTime.getYear() > 2050 || dateTime.getYear() < 1970) {
                dateTime = dateTime.withYear(2024);
            }

            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            System.err.println("Errore durante la conversione del timestamp: " + timestamp);
            return "2024-01-01 00:00:00"; // Valore di fallback
        }
    }
}
