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

import java.time.Instant;
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
    private PlaceRepository placeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Review addReview(String username, String placeName, int stars, String text) {
        // Validazione dei parametri
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Il rateo deve essere compreso tra 1 e 5.");
        }

        // Creazione del nuovo oggetto Review
        Review review = new Review();
        review.setId(UUID.randomUUID().toString()); // Genera un ID unico come stringa e non un tipo ObjectId
        review.setPlace_name(placeName);
        review.setUser(username);
        review.setStars(stars);
        review.setText(text);
        review.setTimestamp(LocalDateTime.now().toString());// questo non mette la Z
        review.setReported(false);


        // Salva la recensione
        Review savedReview = reviewRepository.save(review);

        // Aggiorna la media e il numero totale di recensioni
        placeService.updateReviewSummary(placeName);

        return savedReview;

    }

    public String reportReview(String place_name, String username, String timestamp) {
        // Crea una query per cercare la recensione in base ai parametri
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("place_name").is(place_name)
                    .and("user").is(username)
                    .and("timestamp").is(timestamp));

            System.out.println("Query costruita: " + query.toString());


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


    public List<ReviewsReportedDTO> showReviewReported() {
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


/*
    public String deleteReview(String reviewId) {
        // Cerca la review per ottenere i dettagli
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));

        // Elimina la review
        reviewRepository.deleteById(reviewId);

        // Decrementa il conteggio delle recensioni per il posto associato
        decrementReviewCount(review.getPlace_name());

        return "Review successfully deleted";
    }


    private void decrementReviewCount(String placeName) {
        // Query per trovare il documento corrispondente
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(placeName));

        // Operazione di aggiornamento per decrementare `tot_rev_number`
        Update update = new Update();
        update.inc("reviews_info.tot_rev_number", -1);

        // Esegui l'operazione di aggiornamento
        mongoTemplate.updateFirst(query, update, "Places");

        System.out.println("Decremented tot_rev_number for place: " + placeName);
    }*/
    public String deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + reviewId));
        //metodo predefinito
        reviewRepository.deleteById(reviewId);
        //query per decrementare
        placeService.updateReviewSummary(review.getPlace_name());
        return "Review successfully deleted and count decremented";
    }




    public List<ControversialPlaceDTO> findControversialPlaces()
    {
        return reviewRepository.findMostControversialPlaces();
    }

}
