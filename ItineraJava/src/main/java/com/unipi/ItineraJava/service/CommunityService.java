package com.unipi.ItineraJava.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
import com.unipi.ItineraJava.DTO.ActiveCommunityDTO;
import com.unipi.ItineraJava.DTO.CommunityDTO;
import com.unipi.ItineraJava.model.*;
import com.unipi.ItineraJava.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import com.mongodb.client.result.UpdateResult;
import com.unipi.ItineraJava.exception.ResourceNotFoundException;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.PostSummary;
import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.repository.PostRepository;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private CommunityNeo4jRepository communityNeo4jRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PostRepository postRepository;



    public List<Post> getAllPostsAndComments(String city) {
        List<Post> posts = postRepository.findByCommunity(city);
        if (posts == null) {
            System.out.println("No posts found for city: " + city);
            return new ArrayList<>();
        }

        System.out.println("Number of posts found: " + posts.size());
        return posts;
    }


    public List<PostSummary> getLastTwoPostPreviews(String city) {
        MongoCommunity mongoCommunity = communityRepository.findByCity(city);
        if (mongoCommunity == null || mongoCommunity.getPosts() == null) {
            return new ArrayList<>(); // Prevenire null
        }
        return mongoCommunity.getPost()
                .stream()
                .collect(Collectors.toList());
    }


    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void deleteByName(String name) {
        communityNeo4jRepository.deleteCommunity(name);
        communityRepository.deleteByCity(name);
    }
    
    public void joinCommunity(String username, String city) {
        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }
        if (communityNeo4jRepository.isAlreadyJoined(username, city)) {
            throw new IllegalStateException("User " + username + " has already joined the community: " + city);
        }
    
        
        communityNeo4jRepository.createJoinToCommunity(username, city);
    
        
        System.out.println("User " + username + " successfully joined community: " + city);
    }
    


    public void leaveCommunity(String username, String city) {
        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }
        if (!communityNeo4jRepository.isAlreadyJoined(username, city)) { //se non esiste eccezione
            throw new IllegalStateException("User " + username + " have not joined the community: " + city);
        }

       communityNeo4jRepository.deleteJoinToCommunity(username, city); //elimina la relazione

    
        System.out.println("User " + username + " successfully joined community: " + city);
    }


    public String getMostActiveUserByCommunity(String username, String city) {

        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }
        if (!communityNeo4jRepository.isAlreadyJoined(username, city)) {
            throw new IllegalStateException("User " + username + " has not joined the community: " + city);
        }

        return communityNeo4jRepository.findMostActiveUserByCommunity(city);
    }


    public String getMostActiveCommunity(){
        return communityNeo4jRepository.findMostActiveCommunity();
    }

    public Long getPostCountInCommunity(String city) {
        return communityNeo4jRepository.countPostsInCommunity(city);
    }



    public boolean updateByPost(String city, PostSummary newPostSummary) {
        Query query = new Query(Criteria.where("city").is(city));
        MongoCommunity community = mongoTemplate.findOne(query, MongoCommunity.class);

        if (community == null) {
            return false;
        }
        if (community.getPost() == null || community.getPost().isEmpty()) {
            Update addUpdate = new Update().push("post", newPostSummary);
            UpdateResult result = mongoTemplate.updateFirst(query, addUpdate, MongoCommunity.class);
            return result.getModifiedCount() > 0;
        }
        PostSummary oldestPost = community.getPost().stream()
                .min(Comparator.comparing(PostSummary::getTimestamp))
                .orElse(null);

        if (oldestPost != null) {
            Update removeUpdate = new Update().pull("post", new BasicDBObject("timestamp", oldestPost.getTimestamp()));
            mongoTemplate.updateFirst(query, removeUpdate, MongoCommunity.class);
        }

        Update addUpdate = new Update().push("post", newPostSummary);
        UpdateResult result = mongoTemplate.updateFirst(query, addUpdate, MongoCommunity.class);
        return result.getModifiedCount() > 0;
    }

    public boolean existsByName(String community) {
        return communityRepository.existsByName(community);
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void createCommunity(CommunityDTO communityDTO) {
        try {
            MongoCommunity mongoCommunity = new MongoCommunity();
            mongoCommunity.setName(communityDTO.getCity());
            mongoCommunity.setCity(communityDTO.getCity());
            mongoCommunity.setCreated(LocalDateTime.now().toString());
            mongoCommunity.setId(UUID.randomUUID().toString());
            mongoCommunity.setPost(communityDTO.getPost());

            communityRepository.save(mongoCommunity);
            communityNeo4jRepository.createCommunityNode(communityDTO.getCity());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}