package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Place;
import java.util.List;

public interface PlaceCustomRepository {
    List<Place> findTopPlacesByCity(String city);
}

