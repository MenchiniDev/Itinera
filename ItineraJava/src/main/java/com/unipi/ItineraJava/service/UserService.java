package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.model.Last_post;
import com.unipi.ItineraJava.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unipi.ItineraJava.DTO.CommunityDTO;
import com.unipi.ItineraJava.model.CommunityGraph;
import com.unipi.ItineraJava.model.User;

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
    // DA MODIFICARE CON AUTENTICAZIONE
    public void updateReportedByUsername(String username, boolean reported) {
        userRepository.updateReportedByUsername(username, reported);
    }

    //trova
    public Last_post getLastPostByUsername(String username) {
        //prendo user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

         return user.getLastPost();
    }


    public User updateLastPost(String username, String postBody) {


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        Last_post last_post = new Last_post();
        last_post.setPost_body(postBody);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        last_post.setTimestamp(timestamp);

        user.setLastPost(last_post);

        return userRepository.save(user);
    }


    /////GRAPH
    /// 
    public List<CommunityDTO> getCommunityJoined(String username) {
        List<CommunityDTO> communities = userNeo4jRepository.getCommunityJoined(username);
        communities.forEach(community -> {
            System.out.println("Community retrieved: " + community);
        });
        return communities;
    }
        
}
