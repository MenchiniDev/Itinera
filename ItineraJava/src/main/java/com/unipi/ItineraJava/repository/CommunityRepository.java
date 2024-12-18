package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityRepository extends MongoRepository<Community, String> {}