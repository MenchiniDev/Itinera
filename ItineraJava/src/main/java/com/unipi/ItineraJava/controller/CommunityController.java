package com.unipi.ItineraJava.controller;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.service.CommunityService;
import com.unipi.ItineraJava.service.GraphDbService;

@RestController
@RequestMapping("/Community")
class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private GraphDbService graphDbService;
    @Autowired
    private CommunityRepository mongoCommunityRepository;


    @GetMapping
    public ResponseEntity<List<MongoCommunity>> getAllCommunityNames() {
        List<MongoCommunity> communities = mongoCommunityRepository.findAll();
        System.out.println(communities);
        return ResponseEntity.ok(communities);
    }


    @GetMapping("/{id}")
    public Optional<MongoCommunity> getCommunityById(@PathVariable String id) {
        return communityService.findById(id);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getCommunityDetails(
            @RequestParam String id,
            @RequestParam String username) {
        /*boolean isJoined = graphDbService.isUserJoinedCommunity(username, id);
        if (isJoined) {
            return ResponseEntity.ok(communityService.getAllPostsAndComments(id));
        } else {
            return ResponseEntity.ok(communityService.getLastPostPreview(id));
        }*/
        return null;
    }

    // http://localhost:8080/Community with body
    @PostMapping
    public ResponseEntity<String> createCommunity(
            @RequestHeader("Authorization") String token,
            @RequestBody MongoCommunity mongoCommunity) {
        try {
            if(User.isAdmin(token)) {
                if (mongoCommunityRepository.findByCityAndName(mongoCommunity.getCity(), mongoCommunity.getName()).isPresent()) {
                    return ResponseEntity.status(400).body("A community with the same City and Name already exists.");
                }
                mongoCommunity.setCreated(new Date().toString());
                mongoCommunityRepository.save(mongoCommunity);
                return ResponseEntity.ok("Community created successfully with ID: " + mongoCommunity.getId());
            }else {
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
            } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating community: " + e.getMessage());
        }
    }

    @DeleteMapping("/{name}")
    public void deleteCommunity(@PathVariable String name) {
        communityService.deleteByName(name);
    }



    //join ad una community da parte dello user loggato 
    //http://localhost:8080/Community/joinCommunity/{city}
    @PutMapping("/joinCommunity/{city}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<String> joinCommunity(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

        try {
            // Chiama il servizio per unire l'utente alla community
            communityService.joinCommunity(username, city);

            // Restituisci il messaggio di successo
            return ResponseEntity.ok("User " + username + " successfully joined community: " + city);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Restituisci un messaggio di errore al chiamante
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Gestione del caso in cui l'utente non è autenticato
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
}


     //lasciare una community da parte dello user loggato 
    //http://localhost:8080/Community/joinCommunity/city
    @PutMapping("/leaveCommunity/{city}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> leaveCommunity(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
    
            try {
                // Chiama il servizio per rimuovere l'utente dalla community
                communityService.leaveCommunity(username, city);
    
                // Restituisce il messaggio di successo
                return ResponseEntity.ok("Community successfully left");
    
            } catch (IllegalArgumentException | IllegalStateException ex) {
                // Restituisce il messaggio di errore in caso di eccezione
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
    
        // Se l'utente non è autenticato, restituisce un errore di accesso negato
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
    }
}
