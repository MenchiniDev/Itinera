package com.unipi.ItineraJava.filtering;
// CODICE PER CREAZIONE DI JSON PER LA COLLECTION USER

//LE INFORMAZIONI PERSONALI, COSì COME LE INFORMAZIONI SULL'ACCOUNT SONO INTERAMENTE FITTIZIE, GENERATE TRAMITE LIBRERIA FAKER DI PYTHON
// unica parte non fittizia è l' username dei var utenti, presi da un subreddit dedicato ad alcune delle varie città esaminate

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.List;
import java.util.Map;

public class userCreation {

    public static void main(String[] args) {
        // Percorsi dei file
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\users_data\\user_fakeData_with_time.json";
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\users_data\\users.json";

        try {
            convertJsonArrayToLines(inputFilePath, outputFilePath);
            System.out.println("File generato con successo: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante la conversione del file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertJsonArrayToLines(String inputFilePath, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Leggi il file di input come lista di oggetti
        List<Map<String, Object>> jsonArray = mapper.readValue(new File(inputFilePath), List.class);

        // Scrivi ogni oggetto JSON su una nuova riga
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map<String, Object> jsonObject : jsonArray) {
                writer.write(mapper.writeValueAsString(jsonObject));
                writer.newLine();
            }
        }
    }
}
