package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    // Non sono necessarie query custom per il salvataggio di una recensione
}
