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
            "{ '$project': { 'place_name': 1, 'user': 1, 'text': 1, 'timestamp': 1, _id: 0} }" // Restituisci solo i dati rilevanti
    })
    List<ReviewsReportedDTO> findReportedComments();

    @Query("{'place_name': ?0}")
    List<Review> findByPlace(String name);


    @Aggregation(pipeline = {
            "{ '$match': { 'reported': false } }", // Esclude recensioni segnalate
            "{ '$group': { " +
                    "     '_id': '$placeId', " +
                    "     'placeName': { '$first': '$placeName' }, " +
                    "     'placeCategory': { '$first': '$placeCategory' }, " +
                    "     'averageStars': { '$avg': '$stars' }, " +
                    "     'standardDeviation': { '$stdDevPop': '$stars' }, " +
                    "     'reviews': { '$push': { 'stars': '$stars', 'text': '$text' } }" +
                    "} }", // Raggruppa per luogo e calcola deviazione standard
            "{ '$addFields': { 'variance': { '$pow': ['$standardDeviation', 2] } } }", // Calcola la varianza
            "{ '$sort': { 'placeCategory': 1, 'variance': -1 } }", // Ordina per categoria e varianza decrescente
            "{ '$group': { " +
                    "     '_id': '$placeCategory', " +
                    "     'mostControversial': { '$first': { " +
                    "         'id': '$_id', " +
                    "         'name': '$placeName', " +
                    "         'category': '$placeCategory', " +
                    "         'variance': '$variance', " +
                    "         'averageStars': '$averageStars', " +
                    "         'reviews': '$reviews' } } }" +
                    "}", // Raggruppa per categoria, scegliendo il luogo più controverso
            "{ '$project': { " +
                    "     '_id': 0, " +
                    "     'category': '$_id', " +
                    "     'id': '$mostControversial.id', " +
                    "     'name': '$mostControversial.name', " +
                    "     'variance': '$mostControversial.variance', " +
                    "     'averageStars': '$mostControversial.averageStars', " +
                    "     'reviews': '$mostControversial.reviews' } }" // Proietta i risultati finali
    })
    List<ControversialPlaceDTO> findMostControversialPlaces();


    @Aggregation(pipeline = {
            "{ '$match': { 'place_name': ?0 } }", // Filtra per il nome del posto
            "{ '$group': { '_id': null, 'overall_rating': { '$avg': '$stars' }, 'tot_rev_number': { '$sum': 1 } } }",
            "{ '$project': { '_id': 0, 'overall_rating': { '$round': ['$overall_rating', 3] }, 'tot_rev_number': 1 } }" // Arrotondo la media alle prime 3 cifre decimali
    })
    ReviewSummary calculateReviewSummary(String placeName);

}
