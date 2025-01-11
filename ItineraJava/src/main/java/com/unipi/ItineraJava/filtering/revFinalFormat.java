

/*package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//PARTE 4 PORTO LE REVIEWS NEL LORO FORMATO FINALE PRIMA DI FARLE EMEBEDDED NEI PLACES

public class revFinalFormat {

    public static void main(String[] args) {
        // Percorso del file JSON di origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_50revPerRest.json";
        // Percorso del file JSON di destinazione
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_revFinalForm2.json";

        try {
            modifyReviews(inputFilePath, outputFilePath);
            System.out.println("Modifiche salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante la modifica delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void modifyReviews(String inputFilePath, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Leggi tutte le recensioni dal file JSON
        List<Map<String, Object>> reviews = mapper.readValue(new File(inputFilePath), List.class);

        // Formato della data per la conversione
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        // Modifica ogni recensione
        for (Map<String, Object> review : reviews) {
            // Cambia "date" in "timestamp" e modifica il formato
            String date = (String) review.remove("date");
            if (date != null) {
                try {
                    Date parsedDate = inputDateFormat.parse(date);
                    review.put("timestamp", outputDateFormat.format(parsedDate));
                } catch (ParseException e) {
                    System.err.println("Errore nel parsing della data: " + date);
                }
            }

            // Cambio "review_id" in "rev_id" e rimuovo la parte letterale
            String reviewId = (String) review.remove("review_id");
            if (reviewId != null && reviewId.startsWith("review_")) {
                review.put("rev_id", reviewId.substring(7));
            }

            // Cambia "author_id" in "user"
            String authorId = (String) review.remove("author_id");
            if (authorId != null) {
                review.put("user", authorId);
            }

            // Cambia "rating" in "stars"
            Object rating = review.remove("rating");
            if (rating != null) {
                review.put("stars", rating);
            }

            // Aggiungi "reported" inizializzato a false
            review.put("reported", false);
        }

        // Scrivi le recensioni modificate nel file di destinazione
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            mapper.writeValue(writer, reviews);
        }
    }
}*/
package com.unipi.ItineraJava.filtering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// PARTE 4 PORTO LE REVIEWS NEL LORO FORMATO FINALE
public class revFinalFormat {

    public static void main(String[] args) {
        // Percorso del file origine
        String inputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_50revPerRest.json";
        // Percorso del file destinazione
        String outputFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\progetto_attuale\\itinera\\dataScraping\\reviewsCopiaPerPulizia\\reviews_NY_restaurants\\rist_NY_revFinalFormDue.json";
        // Percorso del file contenente gli orari
        String timeFilePath = "C:\\Users\\nicol\\OneDrive\\Desktop\\AIDE\\Large Scale\\project\\codici_per_dati\\fake_times_RestaurantsRev.txt";

        try {
            modifyReviews(inputFilePath, outputFilePath, timeFilePath);
            System.out.println("Modifiche salvate con successo in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Errore durante la modifica delle recensioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void modifyReviews(String inputFilePath, String outputFilePath, String timeFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Leggi tutte le recensioni dal file JSON
        List<Map<String, Object>> reviews = mapper.readValue(new File(inputFilePath), List.class);

        // Leggi gli orari dal file di testo
        List<String> times = new ArrayList<>();
        try (BufferedReader timeReader = new BufferedReader(new FileReader(timeFilePath))) {
            String line;
            while ((line = timeReader.readLine()) != null) {
                times.add(line.trim());
            }
        }
        if (times.isEmpty()) {
            throw new IllegalStateException("Il file degli orari è vuoto.");
        }

        // Formato della data per la conversione
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");

        // Contatore per tracciare l'indice degli orari
        int timeIndex = 0;

        // Modifica ogni recensione
        for (Map<String, Object> review : reviews) {
            // Cambia "date" in "timestamp" e modifica il formato
            String date = (String) review.remove("date");
            if (date != null) {
                try {
                    Date parsedDate = inputDateFormat.parse(date);
                    String formattedDate = outputDateFormat.format(parsedDate);

                    // Aggiungi l'orario dal file al timestamp
                    String time = times.get(timeIndex % times.size());
                    timeIndex++;
                    review.put("timestamp", formattedDate + " " + time);
                } catch (ParseException e) {
                    System.err.println("Errore nel parsing della data: " + date);
                }
            }

            // Cambio "review_id" in "rev_id" e rimuovo la parte letterale
            String reviewId = (String) review.remove("review_id");
            if (reviewId != null && reviewId.startsWith("review_")) {
                review.put("rev_id", reviewId.substring(7));
            }

            // Cambia "author_id" in "user"
            String authorId = (String) review.remove("author_id");
            if (authorId != null) {
                review.put("user", authorId);
            }
            // Cambia "rating" in "stars"
            Object rating = review.remove("rating");
            if (rating != null) {
                review.put("stars", rating);
            }
            // Modifica il testo della recensione per eliminare riferimenti a new york
            String text = (String) review.get("text");
            if (text != null) {
                text = text.replaceAll("\\bNYC\\b", "this city").replaceAll("\\bNew York\\b", "this city");
                review.put("text", text);
            }
            // Aggiungo "reported" inizializzato a false
            review.put("reported", false);
        }
        // Scrivi le recensioni modificate nel file di destinazione
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            mapper.writeValue(writer, reviews);
        }
    }
}
