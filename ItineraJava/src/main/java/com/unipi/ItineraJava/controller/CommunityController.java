package com.unipi.ItineraJava.controller;


import com.mongodb.client.MongoClient;
import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.service.CommunityService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.service.GraphDbService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.unipi.ItineraJava.documentDb.MongoDBUploader.getMongoConnection;

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


    @PostMapping
    public MongoCommunity createCommunity(@RequestBody MongoCommunity mongoCommunity) {
        return communityService.save(mongoCommunity);
    }

    @PutMapping("/id")
    public MongoCommunity updateCommunity(@RequestBody MongoCommunity mongoCommunity) {
        return null; //TODO: implementare, va aggiunto l'user
    }

    /*
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable String id) {
        communityService.deleteById(id);
    }
     */
}
