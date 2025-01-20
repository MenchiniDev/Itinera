package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findByUsernameAndTimestamp(String postUsername, String postTimestamp);

    @Query("{ 'timestamp': ?0, 'username': ?1, 'Community_name': ?2 }")
    Post findPostByTimestampAndUsernameAndCommunity_name(String timestamp, String username, String community_name);

    @Query("{ 'reported_post': true }")
    List<Post> findByReported_postTrue();

    @Aggregation(pipeline = {
            "{ '$unwind': '$comment' }", // Esplodi l'array dei commenti
            "{ '$match': { 'comment.reported': true } }", // Filtra i commenti segnalati
            "{ '$project': { 'comment.text': 1, 'comment.user': 1, 'comment.timestamp': 1 } }" // Restituisci solo i dati rilevanti
    })
    List<Comment> findReportedComments();
}