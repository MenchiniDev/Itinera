package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.MongoCommunity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommunityRepository extends MongoRepository<MongoCommunity, String> {

        List<MongoCommunity> findAll(); // Questa query restituirà l'oggetto completo

}