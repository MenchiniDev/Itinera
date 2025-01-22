package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.DTO.PostDTO;
import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    Post findByUsernameAndTimestamp(String postUsername, String postTimestamp);


    @Query("{'post':?0 , 'username': ?1, 'community': ?2 }")
    Optional<Post> findPostByTimestampAndUsernameAndCommunity(String body, String username, String community);



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


    List<Post> findByCommunity(String communityName);

    @Aggregation(pipeline = {
            "{ '$match': { 'reportedpost': true } }", // Filtra solo i post segnalati
            "{ '$addFields': { 'reportedComments': { '$size': { '$filter': { " +
                    "       'input': '$comment', " +
                    "       'as': 'c', " +
                    "       'cond': { '$eq': ['$$c.reported', true] } " +
                    "   } } } } }", // Conta i commenti segnalati
            "{ '$sort': { 'reportedComments': -1 } }", // Ordina per numero di commenti segnalati (decrescente)
            "{ '$limit': 10 }", // Limita ai 10 post con più commenti segnalati
            "{ '$project': { '_id': 0, 'id': '$_id', 'community': 1, 'username': 1, 'post': 1, 'reportedComments': 1, 'timestamp': 1 } }" // Proietta i campi necessari
    })
    List<PostSummaryDto> findTopReportedPostsByCommentCount();

    List<Post> findByReportedpostTrue();

    @Aggregation(pipeline = {
            "{ '$match': { 'comment': { '$elemMatch': { 'body': ?0, 'reportedcomment': true } } } }", // Filtra i documenti che contengono almeno un commento che soddisfa i criteri
            "{ '$set': { 'comment': { '$filter': { 'input': '$comment', 'as': 'c', 'cond': { '$and': [ { '$ne': ['$$c.body', ?0] }, { '$eq': ['$$c.reportedcomment', true] } ] } } } } }", // Rimuove i commenti che corrispondono
            "{ '$match': { 'comment': { '$eq': [] } } }", // Filtra i documenti senza commenti residui
            "{ '$unset': 'comment' }", // Elimina il campo 'comment' (opzionale)
            "{ '$merge': { 'into': 'post', 'on': '_id', 'whenMatched': 'replace', 'whenNotMatched': 'fail' } }" // Aggiorna i documenti nel database
    })
    void deleteByText(String text);
}