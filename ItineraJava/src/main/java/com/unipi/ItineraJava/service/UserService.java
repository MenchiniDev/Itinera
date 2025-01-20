package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.model.Last_post;
import com.unipi.ItineraJava.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unipi.ItineraJava.DTO.CommunityDTO;
import com.unipi.ItineraJava.DTO.UserDTO;
import com.unipi.ItineraJava.model.CommunityGraph;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.model.UserGraph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.unipi.ItineraJava.repository.UserNeo4jRepository;

@Service
public class UserService{
   
    private static UserRepository userRepository;
     
    private UserNeo4jRepository userNeo4jRepository;


    @Autowired // necessario altrimenti non consente autenticazione ruolo con funzione User.isAdmin()
    public UserService(UserRepository userRepository, UserNeo4jRepository userNeo4jRepository) {
        this.userRepository = userRepository;
        this.userNeo4jRepository = userNeo4jRepository;
    }

    public static long getNumReview(String username) {
        return userRepository.countReviewsByUser(username);
    }

    public static long getPostCount(String username) {
        return userRepository.countPostsByUser(username);
    }

    public static long getCommentCount(String username) {
        return userRepository.countCommentsByUser(username);
    }

    public List<com.unipi.ItineraJava.model.User> findAll() {
        return userRepository.findAll();
    }

    public Optional<com.unipi.ItineraJava.model.User> findById(String id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    //modifiche bache
    // Trova un utente per username
    public static Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Aggiorna il campo "reported" per uno specifico username
    public void updateReportedByUsername(String username, boolean reported) {
        userRepository.updateReportedByUsername(username, reported);
    }

    public User updateLastPost(String username, String postBody) {


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        Last_post lastPost = new Last_post();
        lastPost.setPost_body(postBody);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        lastPost.setTimestamp(timestamp);

        user.setLastPost(lastPost);

        return userRepository.save(user);
    }


    /////GRAPH
    public List<CommunityDTO> getCommunityJoined(String username) {
        List<CommunityDTO> communities = userNeo4jRepository.getCommunityJoined(username);
        communities.forEach(community -> {
            System.out.println("Community retrieved: " + community);
        });
        return communities;
    }

    
    public void followUser(String user, String userToFollow){
        //controllo se esiste l'utente
        if(!userNeo4jRepository.existsByUsername(userToFollow)){
            throw new IllegalArgumentException("User not found: " + userToFollow);
        }

        //controllo se esiste già la relazione
        if(userNeo4jRepository.existsFollowRelationship(user, userToFollow)){
            throw new IllegalArgumentException("User already followed: " + userToFollow);
        }

        userNeo4jRepository.followUser(user, userToFollow);
    }
     

    public void unfollowUser(String user, String userToUnfollow){

        if(!userNeo4jRepository.existsByUsername(userToUnfollow)){
            throw new IllegalArgumentException("User not found: " + userToUnfollow);
        }

        if(!userNeo4jRepository.existsFollowRelationship(user, userToUnfollow)){
            throw new IllegalArgumentException("User not followed: " + userToUnfollow);
        }
    
        userNeo4jRepository.unfollowUser(user, userToUnfollow);
    }


    public List<UserDTO> getFollowing(String username) {
        return userNeo4jRepository.getFollowing(username);
    }

}
