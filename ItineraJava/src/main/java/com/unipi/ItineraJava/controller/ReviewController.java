package com.unipi.ItineraJava.controller;

import com.unipi.ItineraJava.DTO.ControversialPlaceDTO;
import com.unipi.ItineraJava.DTO.ReviewsReportedDTO;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.ReviewService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; //per lavorare con le mappe nei controller

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;



    @PostMapping
    public ResponseEntity<String> addReview(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody) { //controllo con mappa

        System.out.println("Richiesta ricevuta con "+ requestBody);

        try {
            //System.out.println("sono dentro al primo blocco try");
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if(username == null) {
                return ResponseEntity.badRequest().body("invalid token");

            }

            String place_id = (String) requestBody.get("place_id");
            int stars = (int) requestBody.get("stars");
            String text = (String) requestBody.get("text");

            if (place_id == null || text == null) {
                return ResponseEntity.badRequest().body("Please retry with different parameters");
            }
            //System.out.println("entro in add review");
            Review savedReview = reviewService.addReview(username, place_id, stars, text);
            //System.out.println("uscito da add review");

            if(savedReview == null)
                return ResponseEntity.badRequest().body("invalid review");

            return ResponseEntity.ok("review correctly saved with id " + savedReview.getId());//ritorno id

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Sorry for the inconvenient");
        }
    }
    // delete by id
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@RequestHeader("Authorization") String token,
                                               @PathVariable String reviewId) {
        // Verifica l'utente dal token
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        if (User.isAdmin(token)){

            try {
                //String result = reviewService.deleteReview(reviewId);
                String result = reviewService.deleteReview(reviewId);
                return ResponseEntity.ok(result);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
            }

        }else{
            return ResponseEntity.internalServerError().body("Unauthorized");
        }


    }


    // http://localhost:8080/review/place_id
    @GetMapping("/{placeId}")
    public List<Review> getReviews(
            @PathVariable String placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByName(placeId)).getBody();
    }



    // rimosso il prendere il nome dal token in quanto un tizio reporterebbe la sua  stessa recensione,
    //facendo quello non veniva usato l'username che era passato nella richiesta
    @PutMapping("/report/{reviewId}")

    public ResponseEntity<String> updateReview(
            @RequestHeader("Authorization") String token,
            @PathVariable String reviewId
            )
    {
        return ResponseEntity.ok(reviewService.reportReviewById(reviewId));

    }

    // funzione utilizzabile soltanto da admin
    @GetMapping("/report")
    public ResponseEntity<List<ReviewsReportedDTO>> showReviewReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(reviewService.showReviewReported());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/controversial/places")
    public ResponseEntity<List<ControversialPlaceDTO>> findControversialPlaces(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(reviewService.findControversialPlaces());
        }else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
