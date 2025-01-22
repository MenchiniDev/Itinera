package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.DTO.PostDTO;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findByUsernameAndTimestamp(String postUsername, String postTimestamp);


    @Query("{ 'username': ?0, 'community': ?1 }")
    Optional<PostDTO> findPostByTimestampAndUsernameAndCommunity(String username, String community);



    @Query("{ 'reported_post': true }")
    List<Post> findByReported_postTrue();

    @Aggregation(pipeline = {
            "{ '$unwind': '$comment' }", // Esplodi l'array dei commenti
            "{ '$match': { 'comment.reported': true } }", // Filtra i commenti segnalati
            "{ '$project': { 'comment.text': 1, 'comment.user': 1, 'comment.timestamp': 1 } }" // Restituisci solo i dati rilevanti
    })
    List<Comment> findReportedComments();

    @Query(value = "{ '_id': ?0 }")
    Optional<PostDTO> getPostById(String id);


}