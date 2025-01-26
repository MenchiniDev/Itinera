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

    @Aggregation(pipeline = {
            "{ '$match': { 'comment.reported': true } }", // Filtra i post che contengono almeno un commento segnato
            "{ '$unwind': { 'path': '$comment' } }", // Esplode i commenti
            "{ '$match': { 'comment.reported': true } }", // Filtra solo i commenti segnati come 'reported'
            "{ '$project': {'comment._id': 1, 'comment.username': 1, 'comment.timestamp': 1, 'comment.body': 1 } }" // Proietta i campi desiderati
    })
    List<Comment> findReportedComments();



    @Query(value = "{ '_id': ?0 }")
    Optional<PostDTO> getPostById(String id);


    

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
            "{ '$match': { 'comment.commentId': ?0, 'comment.reported': true } }"
    })
    Post findPostByReportedComment(String commentId);

    @Aggregation(pipeline = {
            // Filtra i documenti che contengono almeno un commento che corrisponde ai criteri
            "{ '$match': { 'comment': { '$elemMatch': { 'body': ?0, 'reported': true } } } }",

            // Usa $pull per rimuovere solo il commento specifico dalla sottocollezione 'comment'
            "{ '$set': { 'comment': { '$filter': { 'input': '$comment', 'as': 'c', 'cond': { '$not': { '$and': [ { '$eq': ['$$c.body', ?0] }, { '$eq': ['$$c.reported', true] } ] } } } } } }",

            // Filtra i documenti che contengono commenti rimasti
            "{ '$match': { 'comment': { '$ne': [] } } }", // Assicura che ci siano ancora commenti

            // Opzionalmente, puoi rimuovere il campo 'comment' se non ci sono più commenti rimasti
            "{ '$unset': 'comment' }"
    })
    void deleteByText(String text);

    @Query(value = "{ '_id': ?0 }")
    Optional<Post> findPostByIdForComment(String postId);

    
    List<Post> findByCommunity(String community);


    Post findPostById(String postId);

    Post findPostBy_id(String postId);
}