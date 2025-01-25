package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.DTO.ReviewsReportedDTO;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.ReviewSummary;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import com.unipi.ItineraJava.DTO.ControversialPlaceDTO;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    @Aggregation(pipeline = {
            "{ '$match': { 'reported': true } }", // Filtra le recensioni segnalate
            "{ '$project': { 'place_id': 1, 'user': 1, 'text': 1, 'timestamp': 1, _id: 0} }" // Restituisci solo i dati rilevanti
    })
    List<ReviewsReportedDTO> findReportedComments();


    @Query("{'place_id': ?0}")
    List<Review> findByPlace(String place_id);


    @Aggregation(pipeline = {
            "{ '$match': { 'reported': false } }", // Esclude recensioni segnalate
            "{ '$group': { " +
                    "     '_id': '$place_id', " +
                    "     'averageStars': { '$avg': '$stars' }, " +
                    "     'standardDeviation': { '$stdDevPop': '$stars' } " +
                    "} }", // Raggruppa per luogo e calcola deviazione standard
            "{ '$addFields': { " +
                    "     'variance': { '$round': [{ '$pow': ['$standardDeviation', 2] }, 3] }, " +
                    "     'averageStars': { '$round': ['$averageStars', 3] }, " +
                    "     'standardDeviation': { '$round': ['$standardDeviation', 3] } " +
                    "} }", // Arrotonda varianza, deviazione standard e media a 3 cifre decimali
            "{ '$match': { 'standardDeviation': { '$gt': 1.3 } } }", // Filtra luoghi con deviazione standard > 1.3
            "{ '$sort': { 'variance': -1 } }", // Ordina per varianza decrescente
            "{ '$project': { " +
                    "     '_id': 0, " +
                    "     'id': '$_id', " +
                    "     'variance': 1, " +
                    "     'standardDeviation': 1, " +
                    "     'averageStars': 1 } }" // Proietta id, varianza, deviazione standard e media
    })
    List<ControversialPlaceDTO> findMostControversialPlaces();




    @Aggregation(pipeline = {
            "{ '$match': { 'place_id': ?0 } }", // Filtra per l'id del posto
            "{ '$group': { '_id': null, 'overall_rating': { '$avg': '$stars' }, 'tot_rev_number': { '$sum': 1 } } }",
            "{ '$project': { '_id': 0, 'overall_rating': { '$round': ['$overall_rating', 3] }, 'tot_rev_number': 1 } }" // Arrotondo la media alle prime 3 cifre decimali
    })
    ReviewSummary calculateReviewSummary(String placeId);


}
