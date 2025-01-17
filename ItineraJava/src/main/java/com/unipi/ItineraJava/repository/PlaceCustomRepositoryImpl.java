package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.unipi.ItineraJava.model.MongoPlace;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<MongoPlace> findTopPlacesByCity(String city) {
        Aggregation aggregation = newAggregation(
                match(where("city").is(city)), // Filtra per città
                group("category")              // Raggruppa per categoria
                        .push("$$ROOT").as("places"),
                unwind("places"),              // Esplodi i risultati per categoria
                sort(Sort.Direction.DESC, "places.overall_rating"), // Ordina per rating
                group("places.category")       // Raggruppa di nuovo per categoria
                        .push("places").as("top_places"),
                project()                      // Proietta i primi 2 di ogni categoria
                        .and("top_places").slice(2).as("top_places")
        );

        AggregationResults<MongoPlace> results = mongoTemplate.aggregate(aggregation, "places", MongoPlace.class);
        return results.getMappedResults();
    }

    public List<Review> getReviewsByCityCategoryAndName(String city, String category, String name) {
        Aggregation aggregation = newAggregation(
                Aggregation.match(Criteria.where("city").is(city).and("category").is(category).and("name").is(name)),
                Aggregation.unwind("reviews"),
                sort(Sort.Direction.DESC, "reviews.stars"),
                project("reviews").andExclude("_id")
        );

        AggregationResults<Review> results = mongoTemplate.aggregate(aggregation, "places", Review.class);
        return results.getMappedResults();
    }
}
