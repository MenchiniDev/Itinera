package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.ReviewService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;



    @PostMapping
    public ResponseEntity<String> addReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String placeName,
            @RequestBody int stars,
            @RequestBody String text) {
        try {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if(username == null)
                return ResponseEntity.badRequest().body("invalid token");
            Review savedReview = reviewService.addReview(username, placeName, stars, text);
            if(savedReview == null)
                return ResponseEntity.badRequest().body("invalid review");

            return ResponseEntity.ok("review correctly saved");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // http://localhost:8080/review?city=roma&&category=hotel&&name=x
    @GetMapping
    public List<Review> getReviews(
            @RequestParam String name) {

        return ResponseEntity.ok(reviewService.getReviewsByName(name)).getBody();
    }

    @PutMapping("/report")
    public ResponseEntity<String> updateReview(
            @RequestHeader("Authorization") String token,
            @RequestBody String timestamp,
            @RequestBody String username,
            @RequestBody String placeName
            )
    {
        username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null)
            return ResponseEntity.badRequest().body("invalid token");
        else {
            return ResponseEntity.ok(reviewService.reportReview(timestamp,username,placeName));
        }
    }

    @GetMapping("/report")
    public ResponseEntity<List<Review>> showReviewReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(reviewService.showReviewReported());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
