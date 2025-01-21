package com.unipi.ItineraJava.controller;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import com.unipi.ItineraJava.DTO.UserDTO;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.service.CommunityService;


@RestController
@RequestMapping("/Community")
class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired

    private CommunityNeo4jRepository communityNeo4jRepository;
    @Autowired

    private CommunityRepository mongoCommunityRepository;
    @Autowired
    private User user;


    // http://localhost:8080/Community
    // returns all communities with details
    @GetMapping
    public ResponseEntity<List<MongoCommunity>> getAllCommunity() {
        List<MongoCommunity> communities = mongoCommunityRepository.findAll();
        System.out.println(communities);
        return ResponseEntity.ok(communities);
    }

    // http://localhost:8080/Community/678e41769d6b117cd029652e
    // returns the {id} community with all his data
    @GetMapping("/{id}")
    public Optional<MongoCommunity> getCommunityById(@PathVariable String id) {
        return ResponseEntity.ok(communityService.findById(id)).getBody();
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

    // adds a post inside a community
    // token -> user auth token
    // text -> post body, plain test no json
    // the name of the city
    // http://localhost:8080/Community/Rome
    @PutMapping("/{name}")
    public ResponseEntity<String> updateCommunity(@RequestHeader("Authorization") String token,
                                                  @PathVariable String name,
                                                  @RequestBody String text)
    {
        try{
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if (username == null)
                return ResponseEntity.status(400).body("Invalid token");
            if (text == null)
                return ResponseEntity.status(400).body("Invalid text");
            if(communityService.existsCommunity(name)) {
                return communityService.updateCommunity(username, text, name);
            }else
            {
                return ResponseEntity.status(400).body("Invalid Community name");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // mi restituisce a caso 403
    @PutMapping("/comment/{username}")
    public ResponseEntity<String> addCommentToPost(
            @RequestHeader("Authorization") String token,
            @PathVariable String username, // of the post replying
            @RequestBody String timestamp,// of the post replying
            @RequestBody String comment_body) {
        try {
            String commenterUsername = JwtTokenProvider.getUsernameFromToken(token);
            System.out.println(commenterUsername);
            if (commenterUsername == null)
                return ResponseEntity.internalServerError().body("token invalid");

            Comment comment = new Comment();
            comment.setUser(commenterUsername);
            comment.setTimestamp(LocalDateTime.now());
            comment.setText(comment_body);
            comment.setReported(false);

            Post updatedPost = communityService.addCommentToPost(username, timestamp, commenterUsername, comment);

            if (updatedPost != null) {
                return ResponseEntity.ok("Commento aggiunto");
            }

            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }





    // http://localhost:8080/Community
    //creates a community checking if the admin is sending the request
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
                communityNeo4jRepository.createCommunityNode(mongoCommunity.getCity());
                return ResponseEntity.ok("Community created successfully with ID: " + mongoCommunity.getId());
            }else {
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
            } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating community: " + e.getMessage());
        }
    }
    // http://localhost:8080/Community/Viareggio
    // deletes a community
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteCommunity(@RequestHeader("Authorization") String token,
                                                  @PathVariable String name) {
        try {
            if (User.isAdmin(token)) {
                communityService.deleteByName(name);
            } else {
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
            return ResponseEntity.ok("Community deleted successfully");
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error deleting community: " + e.getMessage());
        }

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
            // Chiamo il servizio per unire l'utente alla community
            communityService.joinCommunity(username, city);

            // Restituisco il messaggio di successo
            return ResponseEntity.ok("User " + username + " successfully joined community: " + city);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Restituisco un messaggio di errore al chiamante
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // Gestione del caso in cui l'utente non è autenticato
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
}


     //lasciare una community  
    //http://localhost:8080/Community/joinCommunity/city
    @PutMapping("/leaveCommunity/{city}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> leaveCommunity(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
    
            try {
                // Chiamo il servizio per rimuovere l'utente dalla community
                communityService.leaveCommunity(username, city);
    
                // Restituisco il messaggio di successo
                return ResponseEntity.ok("Community successfully left");
    
            } catch (IllegalArgumentException | IllegalStateException ex) {
                // Restituisco il messaggio di errore in caso di eccezione
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
    
        // Se l'utente non è autenticato, restituisce un errore di accesso negato
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
    }


    @GetMapping("/showMostActiveUser/{city}")
    public ResponseEntity<?> getMostActiveUser(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }

        String username = authentication.getName();
        try {
            String mostActiveUser = communityService.getMostActiveUserByCommunity(username, city);
            if (mostActiveUser == null) {
                return ResponseEntity.ok("Nessun utente trovato per la community " + city);
            }
            return ResponseEntity.ok(mostActiveUser);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the most active user: " + ex.getMessage());
        }
    }
}
