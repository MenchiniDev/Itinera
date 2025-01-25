package com.unipi.ItineraJava.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
import org.springframework.stereotype.Service;

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
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MongoClient mongo;
    @Autowired
    private MongoManagedTypes mongoManagedTypes;

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
        communityNeo4jRepository.deleteCommunity(name);
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


    public String getMostActiveCommunity(){
        return communityNeo4jRepository.findMostActiveCommunity();
    }

    public Long getPostCountInCommunity(String city) {
        return communityNeo4jRepository.countPostsInCommunity(city);
    }

   
    
    public boolean updateByPost(String name, PostSummary newPostSummary) {
        // 1. Trova la Community con i post
        Query query = new Query(Criteria.where("name").is(name));
        MongoCommunity community = mongoTemplate.findOne(query, MongoCommunity.class);
    
        if (community == null) {
            // Nessuna community trovata
            return false;
        }
    
        // 2. Verifica se la lista dei post è vuota
        if (community.getPost() == null || community.getPost().isEmpty()) {
            // Aggiungi direttamente il nuovo PostSummary
            Update addUpdate = new Update().push("post", newPostSummary);
            UpdateResult result = mongoTemplate.updateFirst(query, addUpdate, MongoCommunity.class);
            return result.getModifiedCount() > 0;
        }
    
        // 3. Trova il PostSummary con il timestamp meno recente
        PostSummary oldestPost = community.getPost().stream()
                .min(Comparator.comparing(PostSummary::getTimestamp))
                .orElse(null);
    
        if (oldestPost != null) {
            // 4. Rimuovi il PostSummary più vecchio
            Update removeUpdate = new Update().pull("post", new BasicDBObject("timestamp", oldestPost.getTimestamp()));
            mongoTemplate.updateFirst(query, removeUpdate, MongoCommunity.class);
        }
    
        // 5. Aggiungi il nuovo PostSummary alla lista
        Update addUpdate = new Update().push("post", newPostSummary);
        UpdateResult result = mongoTemplate.updateFirst(query, addUpdate, MongoCommunity.class);
    
        // 6. Restituisci true se almeno un documento è stato modificato
        return result.getModifiedCount() > 0;
    }
    

    

    public Boolean existsCommunity(String name) {
        return communityRepository.existsByCity(name);
    }

    public boolean existsByName(String community) {
        return communityRepository.existsByName(community);
    }

    public void createCommunity(CommunityDTO communityDTO) {
        try {
            MongoCommunity mongoCommunity = new MongoCommunity();
            mongoCommunity.setName(communityDTO.getName());
            mongoCommunity.setCity(communityDTO.getCity());
            mongoCommunity.setCreated(LocalDateTime.now().toString());
            mongoCommunity.setId(UUID.randomUUID().toString());
            mongoCommunity.setPost(new ArrayList<PostSummary>());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}