package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.ReviewSummary;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.repository.PlaceRepository;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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


    // Metodo per aggiornare il numero di reviews nella collezione Places
    public void incrementReviewCount(String placeName) {
        // Query per trovare il documento corrispondente
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(placeName));

        // Operazione di aggiornamento per incrementare `tot_rev_number`
        Update update = new Update();
        update.inc("reviews_info.tot_rev_number", 1);

        // Esegui l'operazione di aggiornamento
        mongoTemplate.updateFirst(query, update, "Places");

        System.out.println("Incremented tot_rev_number for place: " + placeName);
    }




}