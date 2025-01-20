package com.unipi.ItineraJava.repository;

import java.util.Optional;

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

}

