package com.unipi.ItineraJava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.model.Place;
import com.unipi.ItineraJava.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping
    public List<Place> getPlacesByCity(@RequestParam String city) {
        return placeService.getBestPlacesByCity(city);
    }

    @GetMapping("/city")
    public List<Place> getTopPlaces(@RequestParam String city) {
        return placeService.getBestPlacesByCity(city);
    }
}

