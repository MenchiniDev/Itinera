package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.Place;

import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {
    List<Place> findByCityOrderByOverallRatingDesc(String city);

    List<Place> findByCityAndCategoryOrderByOverallRatingDesc(String city, String category);


}

