package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("/city")
    public List<MongoPlace> getTopPlaces(@RequestParam String city) {
        return placeService.getBestPlacesByCity(city);
    }

    @GetMapping("/search")
    public List<MongoPlace> getPlacesByCityAndCategory(
            @RequestParam String city,
            @RequestParam String category) {
        return placeService.getPlacesByCityAndCategory(city, category);
    }

    // http://localhost:8080/places/reviews?city=roma&&category=hotel&&name=x
    @GetMapping("/reviews")
    public List<Review> getReviews(
            @RequestParam String city,
            @RequestParam String category,
            @RequestParam String name) {
        return placeService.getReviewsByCityCategoryAndName(city, category, name);
    }

    // http://localhost:8080/places?city=Rome&category=Restaurant&minRating=4.5
    @GetMapping
    public ResponseEntity<List<MongoPlace>> getPlacesByRating(
            @RequestParam String city,
            @RequestParam String category,
            @RequestParam double minRating) {
        List<MongoPlace> mongoPlaces = placeService.findPlacesByRating(city, category, minRating);

        if (mongoPlaces.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(mongoPlaces);
    }

    @PostMapping("/review")
    public Review addReview(@RequestBody Review review) {
        return null; //TODO: implementare
    }
}

