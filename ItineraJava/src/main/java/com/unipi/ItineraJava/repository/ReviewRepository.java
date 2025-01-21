package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    @Aggregation(pipeline = {
            "{ '$match': { 'reported': true } }", // Filtra le recensioni segnalate
            "{ '$project': { 'place_name': 1, 'user': 1, 'text': 1, 'timestamp': 1 } }" // Restituisci solo i dati rilevanti
    })
    List<Review> findReportedComments();

    @Aggregation(pipeline = {
            "{ '$match': { 'city': ?0, 'category': ?1, 'name': ?2 } }", // Filtra per città, categoria e nome
            "{ '$unwind': '$reviews_info' }", // Esplodi le recensioni
            "{ '$sort': { 'reviews_info.overall_rating': -1 } }", // Ordina per stelle
            "{ '$project': { 'reviews_info': 1, '_id': 0 } }" // Ritorna solo il campo 'reviews'
    })
    List<Review> getReviewsByCityCategoryAndName(String city, String category, String name);
}
