package com.unipi.ItineraJava.repository;

import java.util.List;
import java.util.Optional;

import com.unipi.ItineraJava.DTO.ActiveStatusDTO;
import com.unipi.ItineraJava.DTO.ActiveUserDTO;
import com.unipi.ItineraJava.DTO.ReportedUserDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.unipi.ItineraJava.model.User;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);

    @Query(value = "{ 'username': ?0 }")
    @Update( "{ $set: { 'reported': ?1 } }")
    void updateReportedByUsername(String username, boolean reported);

    @Aggregation(pipeline = {
            "{ $match: { 'username': ?0 } }",
            "{ $group: { _id: '$username', reviewCount: { $sum: 1 } } }"
    })
    long countReviewsByUser(String username);

    @Aggregation(pipeline = {
            "{ $match: { 'user': ?0 } }",
            "{ $group: { _id: '$user', postCount: { $sum: 1 } } }"
    })
    long countPostsByUser(String username);

    @Aggregation(pipeline = {
            "{ $unwind: '$comment' }",
            "{ $match: { 'commenti.user': ?0 } }",
            "{ $group: { _id: '$comment.user', commentCount: { $sum: 1 } } }"
    })
    long countCommentsByUser(String username);

    @Aggregation(pipeline = {
            "{ '$addFields': { " +
                    "   'activityScore': { '$add': [ " +
                    "       { '$multiply': ['$postCount', 2] }, " +
                    "       '$commentCount' ] } } }",
            "{ '$match': { 'active': true } }",
            "{ '$sort': { 'activityScore': -1 } }",
            "{ '$limit': 10 }",
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'username': 1, " +
                    "   'email': 1, " +
                    "   'activityScore': 1, " +
                    "   'postCount': 1, " +
                    "   'commentCount': 1 } }"
    })
    List<ActiveUserDTO> findTopActiveUsers();
    
    Long deleteByUsername(String username);


    @Query(value = "{ 'username': ?0 }", fields = "{ 'active': 1, '_id': 0 }")
    Optional<ActiveStatusDTO> findActiveStatusByUsername(String username);

    @Query(value = "{ 'username': ?0 }")
    @Update(value = "{ '$set': { 'active': ?1 } }")
    void updateActiveStatusByUsername(String username, boolean active);


    @Query(value = "{ 'reported': true }", fields = "{ '_id': 1, 'username': 1, 'email': 1 }")
    List<ReportedUserDTO> findUsersReported();

}

