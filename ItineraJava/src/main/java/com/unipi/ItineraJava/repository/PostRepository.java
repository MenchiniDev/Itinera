package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findByUsernameAndTimestamp(String postUsername, String postTimestamp);
}