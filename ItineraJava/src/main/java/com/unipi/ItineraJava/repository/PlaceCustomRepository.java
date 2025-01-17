package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.MongoPlace;
import java.util.List;

public interface PlaceCustomRepository {
    List<MongoPlace> findTopPlacesByCity(String city);


}

