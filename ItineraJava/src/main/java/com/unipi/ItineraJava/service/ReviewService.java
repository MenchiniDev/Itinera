package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.repository.PlaceRepository;
import com.unipi.ItineraJava.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Review addReview(String username, String placeId, int stars, String text) {
        // Validazione dei parametri
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Il rateo deve essere compreso tra 1 e 5.");
        }

        // Creazione del nuovo oggetto Review
        Review review = new Review();
        review.setPlaceId(placeId);
        review.setUser(username);
        review.setStars(stars);
        review.setText(text);
        review.setTimestamp(Instant.now().toString());
        review.setReported(false);

        placeRepository.calculateReviewSummary();

        // Salvataggio
        return reviewRepository.save(review);
    }

    public String reportReview(String placeId, String username, String timestamp) {
        // Crea una query per cercare la recensione in base ai parametri
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("placeId").is(placeId)
                    .and("user").is(username)
                    .and("timestamp").is(timestamp));

            // Crea l'operazione di aggiornamento per impostare reported su true
            Update update = new Update();
            update.set("reported", true);

            // Esegui l'operazione di aggiornamento
            mongoTemplate.updateFirst(query, update, Review.class);
            return "review reported";
        }catch (Exception e){
            return "error occurred";
        }
    }

    public List<Review> showReviewReported() {
        return reviewRepository.findReportedComments();
    }

    public List<Review> getReviewsByName(String name) {
        try {
            List<Review> reviews = reviewRepository.findByPlace(name);
            System.out.println(reviews.size());
            return reviews.stream()
                    .sorted(Comparator.comparingInt(Review::getStars).reversed())
                    .toList();

        } catch (Exception e) {
            System.err.println("Error happened retrieving reviews: " + e.getMessage());
            return List.of();
        }
    }
}
