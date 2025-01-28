package com.unipi.ItineraJava.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.*;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import com.unipi.ItineraJava.DTO.TopRatedCitiesDTO;

import java.util.ArrayList;
import java.util.List;

public interface PlaceRepository extends MongoRepository<MongoPlace, String> {

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


    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'reviews_info.overall_rating': ?1, 'reviews_info.tot_rev_number': ?2 } }")
    void updateReviewSummary(String placeId, double averageRating, int totalReviews);


    List<MongoPlace> findByCity(String city);


    // trovare un posto tramite l'id
    @Query(value = "{ '_id': ?0 }", exists = true)
    boolean existsById(String id);



    @Aggregation(pipeline = {
            "{ '$group': { " +
                    "   '_id': '$city', " + // Raggruppa per città
                    "   'averageRating': { '$avg': '$reviews_info.overall_rating' }, " + // Calcola la media di overall_rating
                    "   'totalPlaces': { '$sum': 1 } " + // Conta il numero di luoghi nella città
                    "} }",
            "{ '$sort': { 'averageRating': -1 } }", // Ordina per media decrescente
            "{ '$limit': 3 }", // Limita ai primi 3 risultati
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'city': '$_id', " + // Rinomina il campo `_id` come `city`
                    "   'averageRating': 1, " +
                    "   'totalPlaces': 1 } }" // Mostra il totale dei luoghi per città
    })
    List<TopRatedCitiesDTO> findTopRatedCitiesByAverageRating();


}

