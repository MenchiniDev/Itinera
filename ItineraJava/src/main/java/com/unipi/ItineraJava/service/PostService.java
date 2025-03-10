package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.PostSummary;
import com.unipi.ItineraJava.repository.PostNeo4jRepository;

import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;

import com.unipi.ItineraJava.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import com.unipi.ItineraJava.DTO.CommentDTO;
import java.util.Objects;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private CommunityNeo4jRepository communityNeo4jRepository;

    @Autowired
    private PostNeo4jRepository postNeo4jRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private UserService userService;

   

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        try {
            return postRepository.getPostById(id)
                    .map(postDTO -> {
                        Post post = new Post();
                        //post.setId(postDTO.getId());
                        post.setCommunity(postDTO.getCommunity());
                        post.setUsername(postDTO.getUsername());
                        post.setPost(postDTO.getPost());
                        post.setTimestamp(postDTO.getTimestamp());
                        post.setNum_comment(postDTO.getNcomment());
                        post.setReported_post(postDTO.isReported_post());
                        post.setComment(postDTO.getComment());
                        return post;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void deleteById(String id) {
        Post post = postRepository.findPostBy_id(id);
        if(post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        String postId = post.getId();
        postNeo4jRepository.deletePostNode(postId);
        postRepository.deleteById(id);
    }

    public boolean reportPost(String postId) {
        try {
            Post post = postRepository.findPostBy_id(postId);
            post.setReported_post(true);
            postRepository.save(post);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reportComment(String postId, String commentId) {
        Optional<Post> post = postRepository.findPostByIdForComment(postId);
    
        if (post.isPresent() && !post.get().getComment().isEmpty()) {
            Comment comment = post.get().getComment().stream()
                .filter(c -> Objects.equals(c.getCommentId(), commentId)) // Gestisce i valori null
                .findFirst()
                .orElse(null);
    
            if (comment != null) {
                // Modifica il campo `reported` del commento
                comment.setReported(true);
    
                // Salva il post aggiornato
                postRepository.save(post.get());
                return true;
            }
        }
    
        return false;
    }

    public List<Post> getReportedPosts() {
        return postRepository.findByReportedpostTrue();
    }

public List<Comment> showCommentReported() {
    return postRepository.findReportedComments();
}


   
    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Post addCommentToPost(String commenterUsername, String postId, String commentBody) {

        Post post = postRepository.findPostBy_id(postId);
        System.out.println(post);

        if (!communityNeo4jRepository.isAlreadyJoined(commenterUsername, post.getCommunity())) {
            throw new IllegalArgumentException("User has not joined community: " + post.getCommunity());
        }
        if (post.getComment() == null) {
            post.setComment(new ArrayList<>());
        }

        Comment comment = new Comment();
        String commentId = UUID.randomUUID().toString();
        comment.setBody(commentBody);
        comment.setReported(false);
        comment.setUsername(commenterUsername);
        comment.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        comment.setCommentId(commentId);

        
        if (post.getComment() == null) {
            post.setComment(new ArrayList<>());
        }
        post.getComment().add(comment);
        post.setNum_comment(post.getNum_comment() + 1);
        postNeo4jRepository.addCommentToPost(postId, comment.getTimestamp(), commenterUsername, commentId);
        return postRepository.save(post);
    }

    public List<PostSummaryDto> findControversialPosts() {
        return postRepository.findTopReportedPostsByCommentCount();
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void updatePostAfterCommentRemoval(String commentId) {
        
        Post post = postRepository.findPostByReportedComment(commentId);
        if (post == null) {
            System.out.println("Nessun post trovato per il commento specificato.");
            throw new IllegalArgumentException("There is no post with this comment.");
        }
        System.out.println("Commenti nel post:");
        post.getComment().forEach(c -> System.out.println(" - Comment ID: " + c.getCommentId() + ", Body: " + c.getBody()));
        Comment comment1 = post.getComment().stream()
            .filter(c -> Objects.equals(c.getCommentId(), commentId)) // Usa il campo corretto per il confronto
            .findFirst()
            .orElse(null);
    
        if (comment1 == null) {
            System.out.println("Nessun commento trovato.");
            throw new IllegalArgumentException("This comment does not exist.");
        }
        postNeo4jRepository.deleteComment(commentId);

        post.getComment().removeIf(comment -> Objects.equals(comment.getCommentId(), commentId));

        post.setNum_comment(post.getNum_comment() - 1);

        postRepository.save(post);
    }

    
    private String generatePreview(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;
    }

    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public boolean addPost(String community, String username, String postBody) {

        if (!communityNeo4jRepository.existsByCity(community)) {
            throw new IllegalArgumentException("This community does not exists: " + community);
        }

        if (!communityNeo4jRepository.isAlreadyJoined(username, community)) {
            throw new IllegalArgumentException("User has not joined community: " + community);
        }
        
        if(communityService.existsByName(community))
        {   String postId = UUID.randomUUID().toString();
            Post post = new Post();
            post.setId(postId);
            post.setUsername(username);
            post.setCommunity(community);
            post.setTimestamp(String.valueOf(LocalDateTime.now()));
            post.setPost(postBody);
            post.setNum_comment(0);
            post.setReported_post(false);
            post.setComment(new ArrayList<>());
            postNeo4jRepository.createPostNode(postId, generatePreview(postBody), post.getTimestamp(), username, community);
            postRepository.save(post);
            userService.updateLastPost(username,post.getPost());

            PostSummary postSummary = new PostSummary();
            postSummary.setUser(username);
            postSummary.setText(postBody);
            postSummary.setTimestamp(String.valueOf(LocalDateTime.now()));
            if(communityService.updateByPost(community,postSummary))
                return true;
            else
                return false;
            
        } else {
            return false;
        }
    }

}

