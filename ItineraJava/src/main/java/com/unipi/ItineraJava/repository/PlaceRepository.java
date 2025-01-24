package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.*;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PlaceRepository extends MongoRepository<MongoPlace, String> {
    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0 } }", // Filtra per città
            "{ '$group': { '_id': '$category', 'places': { '$push': '$$ROOT' } } }", // Raggruppa per categoria
            "{ '$unwind': '$places' }", // Esplodi i risultati
            "{ '$sort': { 'places.overall_rating': -1 } }", // Ordina per rating
            "{ '$group': { '_id': '$places.category', 'top_places': { '$push': '$places' } } }", // Raggruppa di nuovo
            "{ '$project': { 'top_places': { '$slice': ['$top_places', 2] } } }" // Prendi i primi 2
    })
    List<MongoPlace> findTopPlacesByCity(String city);

    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0 } }",
            "{ '$addFields': { 'overallRating': { '$avg': '$reviews_info.overall_rating' } } }",
            "{ '$sort': { 'overallRating': -1 } }"
    })
    List<MongoPlace> findByCityOrderByOverallRatingDesc(String city);

    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0, 'category': ?1 } }",
            "{ '$addFields': { 'overallRating': { '$avg': '$reviews_info.overall_rating' } } }",
            "{ '$sort': { 'overallRating': -1 } }"
    })
    ArrayList<MongoPlace> findByCityAndCategoryOrderByOverallRating(String city, String category);








    @Query("{ 'name': ?0 }")
    @Update("{ '$set': { 'reviews_info.overall_rating': ?1, 'reviews_info.tot_rev_number': ?2 } }")
    void updateReviewSummary(String placeName, double averageRating, int totalReviews);





    List<MongoPlace> findByCity(String city);


    /*@Query("{ 'name': ?0 }")
    @Update("{ '$inc': { 'reviews_info.tot_rev_number': -1 } }")
    void decrementReviewCount(String placeName);*/


    // trovare un posto tramite il nome
    @Query(value = "{ 'name': ?0 }", exists = true)
    boolean existsByName(String name);


}

