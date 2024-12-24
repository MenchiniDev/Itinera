package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.model.Community;
import com.unipi.ItineraJava.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/communities")
class CommunityController {
    @Autowired
    private CommunityService communityService;

    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Community> getCommunityById(@PathVariable String id) {
        return communityService.findById(id);
    }

    @PostMapping
    public Community createCommunity(@RequestBody Community community) {
        return communityService.save(community);
    }

    @PutMapping("/id")
    public Community updateCommunity(@RequestBody Community community) {
        return null; //TODO: implementare, va aggiunto l'user
    }

    /*
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable String id) {
        communityService.deleteById(id);
    }
     */
}
