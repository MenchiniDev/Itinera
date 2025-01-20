package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.unipi.ItineraJava.model.*;

import java.util.List;

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
            "{ '$match': { 'city': ?0, 'category': ?1, 'name': ?2 } }", // Filtra per città, categoria e nome
            "{ '$unwind': '$reviews' }", // Esplodi le recensioni
            "{ '$sort': { 'reviews.stars': -1 } }", // Ordina per stelle
            "{ '$project': { 'reviews': 1, '_id': 0 } }" // Ritorna solo il campo 'reviews'
    })
    List<Review> getReviewsByCityCategoryAndName(String city, String category, String name);

    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0 } }",
            "{ '$addFields': { 'overallRating': { '$avg': '$reviews.overall_rating' } } }",
            "{ '$sort': { 'overallRating': -1 } }"
    })
    List<MongoPlace> findByCityOrderByOverallRatingDesc(String city);

    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0, 'category': ?1 } }",
            "{ '$addFields': { 'overallRating': { '$avg': '$reviews.overall_rating' } } }",
            "{ '$sort': { 'overallRating': -1 } }"
    })
    List<MongoPlace> findByCityAndCategoryOrderByOverallRating(String city, String category);

    @Aggregation(pipeline = {
            "{ '$unwind': '$reviews' }",
            "{ '$group': { '_id': '$name', 'averageRating': { '$avg': '$reviews.overall_rating' }, 'totalReviews': { '$sum': 1 } } }",
            "{ '$project': { '_id': 0, 'name': '$_id', 'averageRating': 1, 'totalReviews': 1 } }"
    })
    void calculateReviewSummary();


}

