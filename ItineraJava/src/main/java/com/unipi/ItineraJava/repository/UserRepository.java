package com.unipi.ItineraJava.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query; // b
import org.springframework.data.mongodb.repository.Update; //b
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.User; //b

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username); //b

    @Query(value = "{ 'username': ?0 }") //b
    @Update( "{ $set: { 'reported': ?1 } }") //b
    void updateReportedByUsername(String username, boolean reported);//b

    @Aggregation(pipeline = {
            "{ $match: { 'user': ?0 } }",               // Filtro per l'utente specificato
            "{ $group: { _id: '$user', reviewCount: { $sum: 1 } } }"  // Conta il numero di recensioni
    })
    long countReviewsByUser(String username);

    @Aggregation(pipeline = {
            "{ $match: { 'user': ?0 } }",               // Filtro per l'utente specificato
            "{ $group: { _id: '$user', postCount: { $sum: 1 } } }"  // Conta il numero di post
    })
    long countPostsByUser(String username);

    @Aggregation(pipeline = {
            "{ $unwind: '$commenti' }",                  // Srotola la lista di commenti
            "{ $match: { 'commenti.user': ?0 } }",       // Filtro per l'utente specificato nei commenti
            "{ $group: { _id: '$commenti.user', commentCount: { $sum: 1 } } }"  // Conta il numero di commenti
    })
    long countCommentsByUser(String username);
}

