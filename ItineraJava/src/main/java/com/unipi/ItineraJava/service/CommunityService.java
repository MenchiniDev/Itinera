package com.unipi.ItineraJava.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.unipi.ItineraJava.model.User;
import jdk.jfr.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unipi.ItineraJava.exception.ResourceNotFoundException;
import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;
import com.unipi.ItineraJava.repository.CommunityRepository;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private CommunityNeo4jRepository communityNeo4jRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private User user;

    public Optional<MongoCommunity> findById(String id) {
        return communityRepository.findById(id);
    }

    public MongoCommunity save(MongoCommunity mongoCommunity) {
        return communityRepository.save(mongoCommunity);
    }

    public void deleteById(String id) {
        communityRepository.deleteById(id);
    }

    public List<Post> getAllPostsAndComments(String communityId) {
        MongoCommunity mongoCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return mongoCommunity.getPosts(); // Include automaticamente i commenti nei post
    }

    public Post getLastPostPreview(String communityId) {
        MongoCommunity mongoCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return mongoCommunity.getPosts()
                .stream()
                .max(Comparator.comparing(Post::getTimestamp))
                .orElse(null); // Ritorna l'ultimo post o null se non esistono post
    }

    public void deleteByName(String name) {
        communityRepository.deleteByCity(name);
    }



    //////// GRAPH
    
    public void joinCommunity(String username, String city) {
        // Verifico se la community esiste
        if (!communityNeo4jRepository.existsByCity(city)) {
            // Lancio un'eccezione con un messaggio personalizzato
            throw new IllegalArgumentException("Community not found: " + city);
        }
    
        // Verifico se la relazione esiste già
        if (communityNeo4jRepository.isAlreadyJoined(username, city)) {
            // Lancio un'eccezione con un messaggio personalizzato
            throw new IllegalStateException("User " + username + " has already joined the community: " + city);
        }
    
        // Se i controlli sono positivi, creo la relazione
        communityNeo4jRepository.createJoinToCommunity(username, city);
    
        
        System.out.println("User " + username + " successfully joined community: " + city);
    }
    


    public void leaveCommunity(String username, String city) {

      //Verifico se la community esiste
        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }
        //Verifico se la relazione esiste o no
        if (!communityNeo4jRepository.isAlreadyJoined(username, city)) { //se non esiste eccezione
            throw new IllegalStateException("User " + username + " have not joined the community: " + city);
        }

       communityNeo4jRepository.deleteJoinToCommunity(username, city); //elimina la relazione

        // (opzionale) Log per debug
        System.out.println("User " + username + " successfully joined community: " + city);
    }

    public ResponseEntity<String> updateCommunity(String username, String text, String name) {
        try {
            Post post = new Post();
            post.setPost_body(text);
            post.setCommunity_name(name);
            post.setUsername(username);

            String currentTimestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            post.setTimestamp(currentTimestamp);

            post.setNum_comment(0);
            post.setReported_post(false);
            post.setComment(null);
            userService.updateLastPost(username,post.getPost_body());
            if (communityRepository.updateMongoCommunityByPost())
                return ResponseEntity.ok("post created");
            else
                return ResponseEntity.internalServerError().body("error creating post");
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("error creating post");
        }

    }

    public boolean existsCommunity(String name) {
        return communityRepository.findByCity(name);
    }
}