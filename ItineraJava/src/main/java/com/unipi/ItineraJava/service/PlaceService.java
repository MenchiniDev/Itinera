package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unipi.ItineraJava.repository.PlaceCustomRepositoryImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceCustomRepositoryImpl placeCustomRepository;

    public List<MongoPlace> getBestPlacesByCity(String city) {
        return placeRepository.findByCityOrderByOverallRatingDesc(city);
    }

    public List<MongoPlace> getPlacesByCityAndCategory(String city, String category) {
        return placeRepository.findByCityAndCategoryOrderByOverallRating(city, category);
    }

    public List<MongoPlace> findPlacesByRating(String city, String category, double minRating) {
        // Recupera i luoghi in base alla città e alla categoria
        List<MongoPlace> mongoPlaces = placeRepository.findByCityAndCategoryOrderByOverallRating(city, category);

        // Filtra in base al rateo medio
        return mongoPlaces.stream()
                .filter(place -> place.getAverageRating() >= minRating)
                .collect(Collectors.toList());
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