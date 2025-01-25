package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.service.PlaceService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    // http://localhost:8080/place/Amsterdam
    // returns a list with all places ordered by decrescent rating
    //funzionante sabato
    @GetMapping("/{city}")
    public List<MongoPlace> getTopPlaces(@PathVariable String city) {
        return ResponseEntity.ok(placeService.getBestPlacesByCity(city)).getBody();
    }

    // http://localhost:8080/place/search?city=Amsterdam&&category=Hotel
    // returns all category places in the city ordered by rating

    @GetMapping("/search/{city}/{category}")
    public List<MongoPlace> getPlacesByCityAndCategory(
            @PathVariable String city,
            @PathVariable String category) {
        try {
            return ResponseEntity.ok(placeService.getPlacesByCityAndCategory(city, category)).getBody();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // http://localhost:8080/place?city=Rome&category=Restaurant&minRating=4.5
    // returns correctly ordered the category of a city by order
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
}

