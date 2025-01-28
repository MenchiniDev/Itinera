package com.unipi.ItineraJava.service;

import com.unipi.ItineraJava.DTO.ControversialPlaceDTO;
import com.unipi.ItineraJava.DTO.ReviewsReportedDTO;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.repository.PlaceRepository;
import com.unipi.ItineraJava.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Review addReview(String username, String placeId, int stars, String text) {
        if (!placeService.doesPlaceExist(placeId)) {
            throw new IllegalArgumentException("The place with id'" + placeId + "' does not exist.");
        }

        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("rating should be between 1 and 5.");
        }

        Review review = new Review();
        review.setId(UUID.randomUUID().toString()); // Genera un ID unico come stringa e non un tipo ObjectId
        review.setPlace_id(placeId);
        review.setUser(username);
        review.setStars(stars);
        review.setText(text);
        review.setTimestamp(LocalDateTime.now().toString());// questo non mette la Z
        review.setReported(false);

        Review savedReview = reviewRepository.save(review);

        placeService.updateReviewSummary(placeId);

        return savedReview;

    }

    public String reportReviewById(String reviewId) {
        try {

            if (!reviewRepository.existsById(reviewId)) {
                return "Review with ID " + reviewId + " does not exist.";
            }

            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(reviewId));

            System.out.println("Query costruita: " + query);

            Update update = new Update();
            update.set("reported", true);

            mongoTemplate.updateFirst(query, update, Review.class);
            return "review reported";
        }catch (Exception e){
            return "error occurred in ReportReviewById"+ e.getMessage();
        }
    }


    public List<ReviewsReportedDTO> showReviewReported() {
        return reviewRepository.findReportedComments();
    }


    public List<Review> getReviewsById(String place_id) {
        try {
            List<Review> reviews = reviewRepository.findByPlace(place_id);
            System.out.println(reviews.size());
            return reviews.stream()
                    .sorted(Comparator.comparingInt(Review::getStars).reversed())
                    .toList();

        } catch (Exception e) {
            System.err.println("Error happened retrieving reviews: " + e.getMessage());
            return List.of();
        }
    }


    public String deleteReview(String reviewId) {
        try{
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));
        reviewRepository.deleteById(reviewId);
        placeService.updateReviewSummary(review.getPlace_id());
        return "Review successfully deleted and count decremented";
        }catch (Exception e){
            return "Error occurred in deleteReview"+ e.getMessage();
        }
    }




    public List<ControversialPlaceDTO> findControversialPlaces()
    {
        return reviewRepository.findMostControversialPlaces();
    }

}
