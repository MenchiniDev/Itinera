package com.unipi.ItineraJava.service;


import com.mongodb.client.MongoClient;
import com.unipi.ItineraJava.exception.ResourceNotFoundException;
import com.unipi.ItineraJava.model.MongoCommunity;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

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
}