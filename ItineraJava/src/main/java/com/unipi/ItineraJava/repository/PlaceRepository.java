package com.unipi.ItineraJava.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.MongoPlace;

import java.util.List;

public interface PlaceRepository extends MongoRepository<MongoPlace, String> {
    List<MongoPlace> findByCityOrderByOverallRatingDesc(String city);

    List<MongoPlace> findByCityAndCategoryOrderByOverallRating(String city, String category);

}

