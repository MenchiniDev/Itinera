package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.service.GraphDbService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/communities")
class CommunityController {

    @Autowired
    private CommunityService communityService;
    @Autowired
    private GraphDbService graphDbService;

    @GetMapping
    public List<MongoCommunity> getAllCommunities() {
        System.out.println("getAllCommunities");

        return communityService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<MongoCommunity> getCommunityById(@PathVariable String id) {
        return communityService.findById(id);
    }

    @GetMapping("/communities")
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
