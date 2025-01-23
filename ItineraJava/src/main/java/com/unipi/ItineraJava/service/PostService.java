package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.DTO.PostDTO;
import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.PostNeo4jRepository;

import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;

import com.unipi.ItineraJava.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostService {

    @Autowired
    private CommunityNeo4jRepository communityNeo4jRepository;

    @Autowired
    private PostNeo4jRepository postNeo4jRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CommunityService communityService;

    private final AtomicLong postCounter = new AtomicLong(200500);

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        try {
            return postRepository.getPostById(id)
                    .map(postDTO -> {
                        Post post = new Post();
                        post.setId(postDTO.getId());
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


    public Post save(Post post) {
        return postRepository.save(post);
    }

    public void deleteById(String id) {
        
        Post post = postRepository.findById(id).orElse(null);
        Long postId = post.getPostId();
        postNeo4jRepository.deletePostNode(postId);
        postRepository.deleteById(id);
    }

    public boolean reportPost(String body, String user, String community) {
        try {
            System.out.println(postRepository.findPostByTimestampAndUsernameAndCommunity(body, user, community));

            Optional<Post> postDTO = postRepository.findPostByTimestampAndUsernameAndCommunity(body, user, community);

            System.out.println(postDTO.isPresent());
            if (postDTO.isPresent()) {
                Post dto = postDTO.get();
                dto.setReported_post(true);
                postRepository.save(dto);
                return true;
            }

            System.out.println("Non trovato");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reportComment(String community, String user, String text) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("community").is(community));
            query.addCriteria(Criteria.where("comment.username").is(user));
            query.addCriteria(Criteria.where("comment.body").is(text));


            Update update = new Update();
            update.set("comment.$.reportedcomment", true);

            mongoTemplate.updateFirst(query, update, Post.class);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Post> getReportedPosts() {
        return postRepository.findByReportedpostTrue();
    }

    public List<Comment> showCommentReported() {
        return postRepository.findReportedComments();
    }


    public List<Post> findByCommunity(String communityName) {
        return postRepository.findByCommunity(communityName);
    }

    public Post addCommentToPost(String postUsername, String postCommunity, String commenterUsername, Comment comment) {

        comment.setReported(false);
        Post post = postRepository.findPostByUsernameAndCommunity(postUsername, postCommunity);
        Long postId = post.getPostId();
        System.out.println(post);

        if (!communityNeo4jRepository.isAlreadyJoined(commenterUsername, postCommunity)) {
            throw new IllegalArgumentException("User has not joined community: " + postCommunity);
        }

        comment.setUsername(commenterUsername);
        comment.setTimestamp(String.valueOf(LocalDateTime.parse(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
        comment.setUsername(commenterUsername);
        comment.setTimestamp(String.valueOf(LocalDateTime.parse(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));


        if (post.getComment() == null) {
            post.setComment(new ArrayList<>());
        }
        post.getComment().add(comment);
        post.setNum_comment(post.getNum_comment() + 1);
        postNeo4jRepository.addCommentToPost(postId, comment.getTimestamp().toString(), commenterUsername);
        return postRepository.save(post);
    }

    public List<PostSummaryDto> findControversialPosts() {
        return postRepository.findTopReportedPostsByCommentCount();
    }

    public void updatePostAfterCommentRemoval(String body) {
        Post post = postRepository.findPostByReportedComment(body);
        if (post == null) {
            System.out.println("Nessun post trovato per il commento specificato.");
            throw new IllegalArgumentException("There is no post with this comment.");
        }

        Comment comment1 = post.getComment().stream().filter(c -> c.getBody().equals(body)).findFirst().orElse(null);
        if (comment1 == null) {
            System.out.println("Nessun commento trovato.");
            throw new IllegalArgumentException("This comment does not exists");
            
        }
        String usernameCommmenter = comment1.getUsername();
        Long postId = post.getPostId();
        String commentTimestamp = comment1.getTimestamp();
        System.out.println("Eliminazione del commento con:");
        System.out.println("Username del commentatore: " + usernameCommmenter);
        System.out.println("Post ID: " + postId);
        System.out.println("Timestamp del commento: " + commentTimestamp);

        postNeo4jRepository.deleteComment(usernameCommmenter, postId, commentTimestamp);

        post.getComment().removeIf(comment -> comment.getBody().equals(body) && comment.isReported());
        post.setNum_comment(post.getNum_comment() - 1);
        postRepository.save(post);
    }

    private String generatePreview(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;
    }


    public boolean addPost(String community, String username, String postBody) {

        if (!communityNeo4jRepository.existsByCity(community)) {
            throw new IllegalArgumentException("This community does not exists: " + community);
        }

        if (!communityNeo4jRepository.isAlreadyJoined(username, community)) {
            throw new IllegalArgumentException("User has not joined community: " + community);
        }


        if(communityService.existsByName(community))
        {   Long postId = postCounter.incrementAndGet();
            Post post = new Post();
            post.setPostId(postId);
            post.setUsername(username);
            post.setCommunity(community);
            post.setTimestamp(String.valueOf(LocalDateTime.now()));
            post.setPost(postBody);
            post.setNum_comment(0);
            post.setReported_post(false);
            post.setComment(new ArrayList<>()); // Inizializza come lista vuota 
            postRepository.save(post);
            postNeo4jRepository.createPostNode(postId, generatePreview(postBody), post.getTimestamp(), username, community);
            return true;
        } else {
            // Community non trovata
            return false;
        }
    }

}

