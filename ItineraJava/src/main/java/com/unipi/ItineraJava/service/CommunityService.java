package com.unipi.ItineraJava.service;


import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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




    //////// GRAPH
    
    public void joinCommunity(String username, String city) {
        // Verifica se la community esiste
        if (!communityNeo4jRepository.existsByCity(city)) {
            // Lancia un'eccezione con un messaggio personalizzato
            throw new IllegalArgumentException("Community not found: " + city);
        }
    
        // Verifica se la relazione esiste già
        if (communityNeo4jRepository.isAlreadyJoined(username, city)) {
            // Lancia un'eccezione con un messaggio personalizzato
            throw new IllegalStateException("User " + username + " has already joined the community: " + city);
        }
    
        // Se i controlli sono positivi, crea la relazione
        communityNeo4jRepository.createJoinToCommunity(username, city);
    
        // Log di successo (opzionale)
        System.out.println("User " + username + " successfully joined community: " + city);
    }
    


    public void leaveCommunity(String username, String city) {
        // Logica per controllare e creare la relazione
      //Verifica se la community esiste
        if (!communityNeo4jRepository.existsByCity(city)) {
            throw new IllegalArgumentException("Community not found: " + city);
        }
        //Verifica se la relazione esiste o no
        if (!communityNeo4jRepository.isAlreadyJoined(username, city)) { //se non esiste eccezione
            throw new IllegalStateException("User " + username + " have not joined the community: " + city);
        }

       communityNeo4jRepository.deleteJoinToCommunity(username, city); //elimina la relazione

        // (opzionale) Log per debug
        System.out.println("User " + username + " successfully joined community: " + city);
    }


}