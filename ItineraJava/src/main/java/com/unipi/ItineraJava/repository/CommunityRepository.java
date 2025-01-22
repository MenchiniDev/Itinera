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

        //todo: architettura
        @Aggregation(pipeline = {
                "{ '$addFields': { " +
                        "   'activityScore': { '$multiply': [ '$postCount', '$averageRating' ] } } }", // Punteggio attività: postCount x averageRating
                "{ '$match': { 'city': ?0 } }", // Filtra per città
                "{ '$sort': { 'activityScore': -1 } }", // Ordina per punteggio decrescente
                "{ '$limit': 10 }", // Limita ai 10 community più attive
                "{ '$project': { " +
                        "   '_id': 0, " +
                        "   'name': 1, " +
                        "   'city': 1, " +
                        "   'activityScore': 1, " +
                        "   'postCount': 1, " +
                        "   'averageRating': 1 } }" // Proietta i campi necessari
        })
        List<ActiveCommunityDTO> findTopActiveCommunities(String city);


        Post save(Post post);
}