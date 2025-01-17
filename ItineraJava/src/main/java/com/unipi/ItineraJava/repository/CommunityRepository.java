package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.MongoCommunity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CommunityRepository extends MongoRepository<MongoCommunity, String> {

}