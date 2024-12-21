package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.Place;
import com.unipi.ItineraJava.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getBestPlacesByCity(String city) {
        //return placeRepository.findByCityOrderByOverallRatingDesc(city);
        return List.of(); //ritorna una lista vuota perchè il metodo sopra mi da errore
    }
}