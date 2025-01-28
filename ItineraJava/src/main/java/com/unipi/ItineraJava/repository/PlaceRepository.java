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
                    "   '_id': '$city', " +
                    "   'averageRating': { '$avg': '$reviews_info.overall_rating' }, " +
                    "   'totalPlaces': { '$sum': 1 } " +
                    "} }",
            "{ '$sort': { 'averageRating': -1 } }",
            "{ '$limit': 3 }",
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'city': '$_id', " +
                    "   'averageRating': 1, " +
                    "   'totalPlaces': 1 } }"
    })
    List<TopRatedCitiesDTO> findTopRatedCitiesByAverageRating();


}

