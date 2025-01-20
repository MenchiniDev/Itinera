package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.repository.PlaceRepository;
import com.unipi.ItineraJava.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

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
}
