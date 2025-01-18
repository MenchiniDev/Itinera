package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String Username);
}

