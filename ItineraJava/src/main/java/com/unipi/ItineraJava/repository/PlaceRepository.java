package com.unipi.ItineraJava.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.Place;

import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {}

