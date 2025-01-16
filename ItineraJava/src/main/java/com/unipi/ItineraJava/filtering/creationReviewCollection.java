package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;
//parte 7 creazione del file per reviews ristoranti
public class creationReviewCollection {

    public static void main(String[] args) {
        // Percorsi dei file
        String inputJsonFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_revFinalFormDue.json";
        String usernamesFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\users_data\\usernames.txt";
        String outputJsonFile = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\reviewsForMongo.json";

        try {
            writeReviewsWithUsernames(inputJsonFile, usernamesFile, outputJsonFile);
            System.out.println("File JSON creato con successo: " + outputJsonFile);
        } catch (Exception e) {
            System.err.println("Errore durante la scrittura del file JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void writeReviewsWithUsernames(String inputJsonFile, String usernamesFile, String outputJsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Carica l'array di oggetti JSON dal file di input
        List<Map<String, Object>> reviews = mapper.readValue(new File(inputJsonFile), List.class);

        // Carica gli usernames dal file di testo
        List<String> usernames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(usernamesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    usernames.add(line.trim());
                }
            }
        }

        // Ciclo per assegnare usernames alle recensioni, a ogni cilo ricomincia dopo i primi 500 e poi arriva fino in fondo
        int totalUsernames = usernames.size();
        int userIndex = 0;
        for (int i = 0; i < reviews.size(); i++) {
            // Determina quale username assegnare
            int cyclePosition = i % totalUsernames; // Posizione nell'attuale ciclo
            if (cyclePosition < 500 && totalUsernames > 500) {
                userIndex = cyclePosition; // Primi 500 usernames
            } else {
                userIndex = (cyclePosition - 500 + 500) % totalUsernames; // Dal 501 in poi
            }

            // Assegna l'username alla recensione
            Map<String, Object> review = reviews.get(i);
            review.put("user", usernames.get(userIndex));
        }

        // Scrivi le recensioni nel file di output (una per riga)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputJsonFile))) {
            for (Map<String, Object> review : reviews) {
                writer.write(mapper.writeValueAsString(review));
                writer.newLine();
            }
        }
    }
}
