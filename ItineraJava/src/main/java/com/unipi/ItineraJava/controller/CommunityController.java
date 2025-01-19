package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.repository.CommunityRepository;
import com.unipi.ItineraJava.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.unipi.ItineraJava.service.GraphDbService;


import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    /*
    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable String id) {
        communityService.deleteById(id);
    }
     */
}
