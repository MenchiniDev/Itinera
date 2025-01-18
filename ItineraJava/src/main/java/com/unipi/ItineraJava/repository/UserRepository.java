package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

import org.springframework.data.mongodb.repository.Query; // b
import org.springframework.data.mongodb.repository.Update; //b

import java.util.Optional; //b

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username); //b

    @Query(value = "{ 'username': ?0 }") //b
    @Update( "{ $set: { 'reported': ?1 } }") //b
    void updateReportedByUsername(String username, boolean reported);//b


}

