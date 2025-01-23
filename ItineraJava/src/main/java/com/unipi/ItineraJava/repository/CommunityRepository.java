package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.DTO.ActiveCommunityDTO;
import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.PostSummary;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends MongoRepository<MongoCommunity, String> {

        void deleteByCity(String city);

        List<MongoCommunity> findAll();

        Optional<MongoCommunity> findByCityAndName(String city, String name);

        MongoCommunity save(MongoCommunity community);

        MongoCommunity findByCity(String city);

        boolean existsByCity(String city);

        Post save(Post post);

        boolean existsByName(String community);
}