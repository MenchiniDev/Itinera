package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.model.ReviewSummary;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.repository.PlaceRepository;
import com.unipi.ItineraJava.repository.ReviewRepository;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.*;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RestTemplateAutoConfiguration restTemplateAutoConfiguration;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ReviewRepository reviewRepository;


    public List<MongoPlace> getBestPlacesByCity(String city) {
        try {
            List<MongoPlace> place;
            place = placeRepository.findByCity(city);
            if (place.isEmpty())
                    return null;
            else
                return placeRepository.findByCityOrderByOverallRatingDesc(city);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




    public List<MongoPlace> getPlacesByCityAndCategory(String city, String category) {
            return placeRepository.findByCityAndCategoryOrderByOverallRating(city, category);
    }

    private Boolean existsCategory(String category) {
        return category.equals("Hotel") || category.equals("Restaurant") || category.equals("Monuments");
    }

    public List<MongoPlace> findPlacesByRating(String city, String category, double minRating) {
        // Recupera i luoghi in base alla città e alla categoria
        List<MongoPlace> mongoPlaces = placeRepository.findByCityAndCategoryOrderByOverallRating(city, category);

        // Controlla se la lista è null o vuota
        if (mongoPlaces == null || mongoPlaces.isEmpty()) {
            return Collections.emptyList();
        }

        // Filtra i luoghi basandoti sul rating medio calcolato
        return mongoPlaces.stream()
                .filter(place -> place.getReviews().getOverall_rating() >= minRating)
                .toList();
    }


    public void updateReviewSummary(String placeId) {
        // Calcola la media e il totale delle recensioni
        ReviewSummary summary = reviewRepository.calculateReviewSummary(placeId);
        //System.out.println("funzione di aggiornamento chiamata");
        System.out.println("Average Rating: " + summary.getOverall_rating());
        System.out.println("Total Reviews: " + summary.getTot_rev_number());

        if (summary != null) {
            //System.out.println("calcolati media e totale recensioni,procedo ad aggiornarli");
            // Estrai i valori calcolati
            double averageRating = summary.getOverall_rating();
            int totalReviews = summary.getTot_rev_number();

            // Aggiorna il documento nella collezione Places
            placeRepository.updateReviewSummary(placeId, averageRating, totalReviews);
            System.out.println("values updated");
        } else {
            System.err.println("No reviews found for place, summary is null : " + placeId);
        }
    }

    //metodo per controllare l'esistenza del posto in questione
    public boolean doesPlaceExist(String placeId) {
        return placeRepository.existsById(placeId);
    }



}