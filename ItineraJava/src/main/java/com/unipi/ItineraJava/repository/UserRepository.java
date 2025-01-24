package com.unipi.ItineraJava.repository;

import java.util.List;
import java.util.Optional;

import com.unipi.ItineraJava.DTO.ActiveStatusDTO;
import com.unipi.ItineraJava.DTO.ActiveUserDTO;
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
            "{ $match: { 'username': ?0 } }",               // Filtro per l'utente specificato
            "{ $group: { _id: '$username', reviewCount: { $sum: 1 } } }"  // Conta il numero di recensioni
    })
    long countReviewsByUser(String username);

    @Aggregation(pipeline = {
            "{ $match: { 'user': ?0 } }",               // Filtro per l'utente specificato
            "{ $group: { _id: '$user', postCount: { $sum: 1 } } }"  // Conta il numero di post
    })
    long countPostsByUser(String username);

    @Aggregation(pipeline = {
            "{ $unwind: '$comment' }",                  // Srotola la lista di commenti
            "{ $match: { 'commenti.user': ?0 } }",       // Filtro per l'utente specificato nei commenti
            "{ $group: { _id: '$comment.user', commentCount: { $sum: 1 } } }"  // Conta il numero di commenti
    })
    long countCommentsByUser(String username);

    @Aggregation(pipeline = {
            "{ '$addFields': { " +
                    "   'activityScore': { '$add': [ " +
                    "       { '$multiply': ['$postCount', 2] }, " + // Ponderazione: post pesano il doppio
                    "       '$commentCount' ] } } }", // Somma pesata di post e commenti
            "{ '$match': { 'active': true } }", // Considera solo utenti attivi
            "{ '$sort': { 'activityScore': -1 } }", // Ordina per punteggio decrescente
            "{ '$limit': 10 }", // Limita ai 10 utenti più attivi
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'username': 1, " +
                    "   'email': 1, " +
                    "   'activityScore': 1, " +
                    "   'postCount': 1, " +
                    "   'commentCount': 1 } }" // Proietta i campi necessari
    })
    List<ActiveUserDTO> findTopActiveUsers();


    
    
    Long deleteByUsername(String username);


    @Query(value = "{ 'username': ?0 }", fields = "{ 'active': 1, '_id': 0 }")
    Optional<ActiveStatusDTO> findActiveStatusByUsername(String username);

    @Query(value = "{ 'username': ?0 }")
    @Update(value = "{ '$set': { 'active': ?1 } }")
    void updateActiveStatusByUsername(String username, boolean active);

}

