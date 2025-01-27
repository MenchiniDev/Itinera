package com.unipi.ItineraJava.controller;


import java.util.List;
import java.util.Map;

import com.unipi.ItineraJava.DTO.CommunityDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unipi.ItineraJava.model.*;

import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.service.CommunityService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;


@RestController
@RequestMapping("/Community")
class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired

    private CommunityNeo4jRepository communityNeo4jRepository;
    @Autowired

    private CommunityRepository mongoCommunityRepository;

    // http://localhost:8080/Community
    // returns all communities with details OK
    @GetMapping
    public ResponseEntity<List<MongoCommunity>> getAllCommunity() {
        List<MongoCommunity> communities = mongoCommunityRepository.findAll();
        return ResponseEntity.ok(communities);
    }

    //http://localhost:8080/Community/details/6796303dc9b98870aea09b22
    // returns the {id} community with all his data OK
    @GetMapping("/details/{city}")
public ResponseEntity<?> getCommunityDetails(@RequestHeader("Authorization") String token,
                                             @PathVariable String city) {
        if(!User.isAdmin(token)) {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            boolean isJoined = communityNeo4jRepository.isAlreadyJoined(username, city);

            if (isJoined) {
                // Restituisce tutti i post e i commenti
                List<Post> posts = communityService.getAllPostsAndComments(city);
                return ResponseEntity.ok(posts);
            } else {
                // Restituisce solo due post riassuntivi
                List<PostSummary> postPreviews = communityService.getLastTwoPostPreviews(city);
                return ResponseEntity.ok(postPreviews);
            }
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
}



    
    // http://localhost:8080/Community
    //creates a community checking if the admin is sending the request OK
    @PostMapping
    public ResponseEntity<String> createCommunity(
            @RequestHeader("Authorization") String token,
            @RequestBody CommunityDTO communityDTO) {
        try {
            if(User.isAdmin(token)) {
                MongoCommunity mongoCommunity = mongoCommunityRepository.findByCity(communityDTO.getCity());
                if (mongoCommunity != null) {
                    return ResponseEntity.status(400).body("A community with the same City and Name already exists.");
                }else {
                    communityService.createCommunity(communityDTO);
                    return ResponseEntity.status(201).body("Community "+communityDTO.getCity()+" successfully.");
                }
            }else {
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
            } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating community: " + e.getMessage());
        }
    }
    // http://localhost:8080/Community/Viareggio
    // deletes a community OK
    @DeleteMapping("/{city}")
    public ResponseEntity<String> deleteCommunity(@RequestHeader("Authorization") String token,
                                                  @PathVariable String city) {
        try {
            if (User.isAdmin(token)) {
                communityService.deleteByName(city);
            } else {
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
            return ResponseEntity.ok("Community deleted successfully");
        }catch(Exception e){
            return ResponseEntity.status(500).body("Error deleting community: " + e.getMessage());
        }
    }



    //join in a community for a logged user
    //http://localhost:8080/Community/joinCommunity/{city} OK
    @PutMapping("/joinCommunity/{city}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<String> joinCommunity(@PathVariable String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

        try {
            communityService.joinCommunity(username, city);
            return ResponseEntity.ok("User " + username + " successfully joined community: " + city);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
}


    //http://localhost:8080/Community/joinCommunity/city
    @PutMapping("/leaveCommunity/{city}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> leaveCommunity(@RequestHeader("Authorization") String token,
                                                @PathVariable String city) {
        if(!User.isAdmin(token)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();

                try {
                    communityService.leaveCommunity(username, city);
                    return ResponseEntity.ok("Community successfully left");

                } catch (IllegalArgumentException | IllegalStateException ex) {
                    return ResponseEntity.badRequest().body(ex.getMessage());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
    }

    //http://localhost:8080/Community/showMostActiveUser/Amsterdam
    // OK
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
                return ResponseEntity.ok("Community " + city + "has no users");
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

    // http://localhost:8080/Community/showMostActiveCommunity OK
    @GetMapping("/showMostActiveCommunity")
    public ResponseEntity<?> getMostActiveUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }
        try {
            String mostActiveCommunity = communityService.getMostActiveCommunity();
            if (mostActiveCommunity == null) {
                return ResponseEntity.ok("There is no most active community");
            }
            return ResponseEntity.ok(mostActiveCommunity);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the most active community: " + ex.getMessage());
        }
    }

    // http://localhost:8080/Community/postCount/Amsterdam OK
    @GetMapping("/postCount/{city}")
    public ResponseEntity<?> getPostCount(@PathVariable String city) {
        try {
            Long postCount = communityService.getPostCountInCommunity(city);
            return ResponseEntity.ok(Map.of("community", city, "postCount", postCount));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving post count: " + ex.getMessage());
        }
    }
}
