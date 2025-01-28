package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.DTO.PlaceDTO;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.MongoPlace;
import com.unipi.ItineraJava.service.PlaceService;
import com.unipi.ItineraJava.DTO.TopRatedCitiesDTO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/place")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    // http://localhost:8080/place/Amsterdam
    // returns a list with all places ordered by decrescent rating OK
    @GetMapping("/{city}")
    public List<MongoPlace> getTopPlaces(@PathVariable String city) {
        return ResponseEntity.ok(placeService.getBestPlacesByCity(city)).getBody();
    }

    // http://localhost:8080/place/search?city=Amsterdam&&category=Hotel
    // returns all category places in the city ordered by rating OK
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
    // returns correctly ordered the category of a city by order OK
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
    //http://localhost:8080/place
    // {
    //    "name":"Hotel Maggico",
    //    "address":"via degli eleocorni 55",
    //    "city":"Pontecorvo",
    //    "category":"Hotel"
    //} OK
    @PostMapping
    public ResponseEntity<String> createPlace(
            @RequestHeader("Authorization") String token,
            @RequestBody PlaceDTO place) {
        if(User.isAdmin(token))
        {
            placeService.addPlace(place);
            return ResponseEntity.ok("Place created");
        }else
        {
            return ResponseEntity.badRequest().body("Unauthorized, log as an admin");
        }
    }

    @GetMapping("/topCities")
    public ResponseEntity<List<TopRatedCitiesDTO>> getTopRatedCities(@RequestHeader("Authorization") String token) {
        if(User.isAdmin(token))
        {
            List<TopRatedCitiesDTO> topCities = placeService.getTopRatedCities();
            if (topCities.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            return ResponseEntity.ok(topCities);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }



}

