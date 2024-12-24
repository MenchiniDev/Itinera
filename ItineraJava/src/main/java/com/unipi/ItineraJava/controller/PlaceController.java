package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.Place;
import com.unipi.ItineraJava.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping //forse metodo ridondante
    public List<Place> getPlacesByCity(@RequestParam String city) {
        return placeService.getBestPlacesByCity(city);
    }

    @GetMapping("/city")
    public List<Place> getTopPlaces(@RequestParam String city) {
        return placeService.getBestPlacesByCity(city);
    }

    @GetMapping("/search")
    public List<Place> getPlacesByCityAndCategory(
            @RequestParam String city,
            @RequestParam String category) {
        return placeService.getPlacesByCityAndCategory(city, category);
    }

    @GetMapping("/reviews")
    public List<Review> getReviews(
            @RequestParam String city,
            @RequestParam String category,
            @RequestParam String name) {
        return placeService.getReviewsByCityCategoryAndName(city, category, name);
    }

    @PostMapping("/review")
    public Review addReview(@RequestBody Review review) {
        return null; //TODO: implementare
    }
}

