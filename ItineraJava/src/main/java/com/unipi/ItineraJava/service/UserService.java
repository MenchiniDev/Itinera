package com.unipi.ItineraJava.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.unipi.ItineraJava.DTO.*;

import com.unipi.ItineraJava.model.Last_post;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.repository.UserNeo4jRepository;
import com.unipi.ItineraJava.repository.UserRepository;

@Service
public class UserService{
    
    private static UserRepository userRepository;
    
    private UserNeo4jRepository userNeo4jRepository;


    @Autowired // necessario altrimenti non consente autenticazione ruolo con funzione User.isAdmin()
    public UserService(UserRepository userRepository, UserNeo4jRepository userNeo4jRepository) {
        this.userRepository = userRepository;
        this.userNeo4jRepository = userNeo4jRepository;
    }

    public static String getNumReview(String username) {
        Long count = userRepository.countReviewsByUser(username);
        return String.valueOf(count);
    }

    public static long getPostCount(String username) {
        return userRepository.countPostsByUser(username);
    }

    public static long getCommentCount(String username) {
        return userRepository.countCommentsByUser(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

     
    public void deleteById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        String username = user.getUsername();
        userNeo4jRepository.deleteUserNode(username);
        userRepository.deleteById(id);
    }
    
    // Trova un utente per username
    public static Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Aggiorna il campo "reported" per uno specifico username
    public void updateReportedByUsername(String username, boolean reported) {
        userRepository.updateReportedByUsername(username, reported);
    }

    //aggiorna il campo "active" per uno specifico username
    public void deactivateUser(String username) {
        // Aggiorna il campo active a false
        userRepository.updateActiveStatusByUsername(username, false);
    }


    //verifica lo stato del campo active di un utente
    public boolean isUserActive(String username) {
        // Recupera il valore del campo 'active'
        return userRepository.findActiveStatusByUsername(username).map(ActiveStatusDTO::isActive) //restituisce direttamente un bool
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    //trova
    public Last_post getLastPostByUsername(String username) {
        //prendo user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

         return user.getLastPost();
    }


    public User updateLastPost(String username, String postBody) {


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        Last_post last_post = new Last_post();
        last_post.setPost_body(postBody);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        last_post.setTimestamp(timestamp);

        user.setLastPost(last_post);

        return userRepository.save(user);
    }


    /////GRAPH
    public List<CommunityDTO> getCommunityJoined(String username) {
        List<CommunityDTO> communities = userNeo4jRepository.getCommunityJoined(username);
        communities.forEach(community -> {
            System.out.println("Community retrieved: " + community);
        });
        return communities;
    }

    
    public void followUser(String user, String userToFollow){
        //controllo se esiste l'utente
        if(!userNeo4jRepository.existsByUsername(userToFollow)){
            throw new IllegalArgumentException("User not found: " + userToFollow);
        }

        //controllo se esiste già la relazione
        if(userNeo4jRepository.existsFollowRelationship(user, userToFollow)){
            throw new IllegalArgumentException("User already followed: " + userToFollow);
        }

        userNeo4jRepository.followUser(user, userToFollow);
    }
     

    public void unfollowUser(String user, String userToUnfollow){

        if(!userNeo4jRepository.existsByUsername(userToUnfollow)){
            throw new IllegalArgumentException("User not found: " + userToUnfollow);
        }

        if(!userNeo4jRepository.existsFollowRelationship(user, userToUnfollow)){
            throw new IllegalArgumentException("User not followed: " + userToUnfollow);
        }
    
        userNeo4jRepository.unfollowUser(user, userToUnfollow);
    }


    public List<UserDTO> getFollowing(String username) {
        return userNeo4jRepository.getFollowing(username);
    }

    public List<String> getSuggestedUsernames(String username) {
        return userNeo4jRepository.findSuggestedUsernames(username);
    }

    public List<String> getSuggestedCommunities(String username){
        return userNeo4jRepository.findSuggestedCommunities(username);
    }

    public List<PostSuggestionDto> getSuggestedPosts(String username) {
        return userNeo4jRepository.findSuggestedPosts(username);
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public boolean deleteByUsername(String username) {
        
        if(!userNeo4jRepository.existsByUsername(username)){
            throw new IllegalArgumentException("User not found: " + username);
        }

        userNeo4jRepository.deleteUserNode(username);

        if((!userNeo4jRepository.existsByUsername(username)) && (userRepository.deleteByUsername(username) > 0)){
            return true;
        }else{
            return false;
        }
        
    }

    public List<ActiveUserDTO> findTopActiveUsers()
    {
        return userRepository.findTopActiveUsers();
    }

    public List<ReportedUserDTO> getReportedUsers() {
        return userRepository.findUsersReported();
    }

}
