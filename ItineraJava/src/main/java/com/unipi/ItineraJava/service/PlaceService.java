package com.unipi.ItineraJava.service;
//commento a cas per pushare
import com.unipi.ItineraJava.model.Place;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import com.unipi.ItineraJava.repository.PlaceCustomRepositoryImpl;

import java.util.Comparator;
import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceCustomRepositoryImpl placeCustomRepository;

    public List<Place> getBestPlacesByCity(String city) {
        return placeRepository.findByCityOrderByOverallRatingDesc(city);
    }

    public List<Place> getPlacesByCityAndCategory(String city, String category) {
        return placeRepository.findByCityAndCategoryOrderByOverallRatingDesc(city, category);
    }

    public List<Review> getReviewsByCityCategoryAndName(String city, String category, String name) {
        try {
            List<Review> reviews = placeCustomRepository.getReviewsByCityCategoryAndName(city, category, name);
            return reviews.stream()
                    .sorted(Comparator.comparingInt(Review::getStars).reversed())
                    .toList();

        } catch (Exception e) {
            System.err.println("Error happened retrieving reviews: " + e.getMessage());
            return List.of();
        }
    }


}