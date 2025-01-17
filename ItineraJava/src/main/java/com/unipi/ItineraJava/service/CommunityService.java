package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.exception.ResourceNotFoundException;
import com.unipi.ItineraJava.model.Community;
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

    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public Optional<Community> findById(String id) {
        return communityRepository.findById(id);
    }

    public Community save(Community community) {
        return communityRepository.save(community);
    }

    public void deleteById(String id) {
        communityRepository.deleteById(id);
    }

    public List<Post> getAllPostsAndComments(String communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return community.getPosts(); // Include automaticamente i commenti nei post
    }

    public Post getLastPostPreview(String communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        return community.getPosts()
                .stream()
                .max(Comparator.comparing(Post::getTimestamp))
                .orElse(null); // Ritorna l'ultimo post o null se non esistono post
    }
}