package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/place/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(
            @RequestParam String username,
            @RequestParam String placeId,
            @RequestParam int stars,
            @RequestParam String text) {
        try {
            // Aggiungi la recensione
            Review savedReview = reviewService.addReview(username, placeId, stars, text);
            return ResponseEntity.ok(savedReview);
        } catch (IllegalArgumentException e) {
            // Gestione degli errori di validazione
            return ResponseEntity.badRequest().body(null);
        }
    }
}
