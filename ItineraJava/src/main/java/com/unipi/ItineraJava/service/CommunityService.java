package com.unipi.ItineraJava.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import com.unipi.ItineraJava.model.*;
import com.unipi.ItineraJava.repository.PostRepository;
import jdk.jfr.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unipi.ItineraJava.exception.ResourceNotFoundException;
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
    private MongoTemplate mongoTemplate;
    @Autowired
    private PostRepository postRepository;

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


    public String getMostActiveUserByCommunity(String username, String city) {

        // Verifica se la community esiste
        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }

        // Verifica se l'utente è un membro della community
        if (!communityNeo4jRepository.isAlreadyJoined(username, city)) {
            throw new IllegalStateException("User " + username + " has not joined the community: " + city);
        }

        // Recupera l'utente più attivo
        return communityNeo4jRepository.findMostActiveUserByCommunity(city);
    }


    //////////

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

            PostSummary postSummary = new PostSummary();
            postSummary.setUser(username);
            postSummary.setText(text);
            postSummary.setTimestamp(currentTimestamp);
            if (updateByPost(name,postSummary))
                return ResponseEntity.ok("post created");
            else
                return ResponseEntity.internalServerError().body("error creating post");
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("error creating post");
        }

    }
    public boolean updateByPost(String name, PostSummary postSummary) {
            Query query = new Query(Criteria.where("name").is(name));
            Update update = new Update().push("post", postSummary);

            UpdateResult result = mongoTemplate.updateFirst(query, update, MongoCommunity.class);

            return result.getModifiedCount() > 0;
    }

    public Boolean existsCommunity(String name) {
        return communityRepository.existsByCity(name);
    }

    public Post addCommentToPost(String postUsername, String postTimestamp, String commenterUsername, Comment comment) {

        comment.setReported(false);
        Post post = postRepository.findByUsernameAndTimestamp(postUsername, postTimestamp);
        System.out.println(post);

        if (post != null) {
            comment.setUser(commenterUsername);
            comment.setTimestamp(LocalDateTime.parse(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));


            if (post.getComment() == null) {
                post.setComment(new ArrayList<>());
            }
            post.getComment().add(comment);
            post.setNum_comment(post.getNum_comment() + 1);
            return communityRepository.save(post);
        }

        throw new IllegalArgumentException("Post not found for username: " + postUsername + " and timestamp: " + postTimestamp);
    }
}