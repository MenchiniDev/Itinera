package com.unipi.ItineraJava.service;

import com.google.gwt.place.shared.Place;
import com.mongodb.client.MongoClient;
import com.unipi.ItineraJava.DTO.PlaceDTO;
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

    public List<MongoPlace> findPlacesByRating(String city, String category, double minRating) {
        List<MongoPlace> mongoPlaces = placeRepository.findByCityAndCategoryOrderByOverallRating(city, category);
        if (mongoPlaces == null || mongoPlaces.isEmpty()) {
            return Collections.emptyList();
        }
        return mongoPlaces.stream()
                .filter(place -> place.getReviews().getOverall_rating() >= minRating)
                .toList();
    }


    public void updateReviewSummary(String placeId) {
        ReviewSummary summary = reviewRepository.calculateReviewSummary(placeId);
        System.out.println("Average Rating: " + summary.getOverall_rating());
        System.out.println("Total Reviews: " + summary.getTot_rev_number());

        double averageRating = summary.getOverall_rating();
        int totalReviews = summary.getTot_rev_number();
        placeRepository.updateReviewSummary(placeId, averageRating, totalReviews);
        System.out.println("values updated");
    }
    public boolean doesPlaceExist(String placeId) {
        return placeRepository.existsById(placeId);
    }


    
    public void addPlace(PlaceDTO place) {
        try {
            MongoPlace mongoPlace = new MongoPlace();
            mongoPlace.setId(UUID.randomUUID().toString());
            mongoPlace.setCity(place.getCity());
            mongoPlace.setName(place.getName());
            mongoPlace.setAddress(place.getAddress());
            mongoPlace.setCategory(place.getCategory());
            mongoPlace.setReviews(new ReviewSummary(0, 0));

            placeRepository.save(mongoPlace);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}