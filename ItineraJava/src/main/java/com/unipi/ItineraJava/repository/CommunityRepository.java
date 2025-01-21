package com.unipi.ItineraJava.repository;

import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.PostSummary;
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

        @Query("{ 'city': { $regex: '^?0$', $options: 'i' } }")
        Boolean findByCity(String city);


        Post save(Post post);
}