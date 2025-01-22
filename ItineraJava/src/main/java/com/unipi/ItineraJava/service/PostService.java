package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.DTO.PostDTO;
import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.configuration.StringToLocalDateTimeConverter;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.PostRepository;
import com.unipi.ItineraJava.repository.PostNeo4jRepository;
import com.unipi.ItineraJava.repository.CommunityNeo4jRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
                        post.setTimestamp(String.valueOf(postDTO.getTimestamp()));
                        post.setNum_comment(postDTO.getNcomment());
                        post.setReported_post(postDTO.isReported_post());
                        post.setComment(postDTO.getComment());
                        return post;
                    });
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }



    public Post save(Post post) {
        return postRepository.save(post);
    }

    public void deleteById(String id) {
        postRepository.deleteById(id);
        Long postId = Long.parseLong(id);
        postNeo4jRepository.deletePostNode(postId);
    }

    public boolean reportPost(String timestamp, String user, String community) {
        try {
            LocalDateTime timestampDate = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            System.out.println(timestampDate);
            System.out.println(user);
            System.out.println(community);

            System.out.println(postRepository.findPostByTimestampAndUsernameAndCommunity(user, community));

            Optional<PostDTO> postDTO = postRepository.findPostByTimestampAndUsernameAndCommunity(user, community);

            System.out.println(postDTO.isPresent());
            if (postDTO.isPresent()) {
                PostDTO dto = postDTO.get();

                Post post = new Post();
                post.setTimestamp(String.valueOf(dto.getTimestamp()));
                post.setUsername(dto.getUsername());
                post.setCommunity(dto.getCommunity());

                postRepository.save(post);
                return true;
            }

            System.out.println("Non trovato");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean reportComment(String timestamp, String user, String text) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("timestamp").is(timestamp));
            query.addCriteria(Criteria.where("comment.user").is(user));
            query.addCriteria(Criteria.where("comment.text").is(text));


            Update update = new Update();
            update.set("comment.$.reported", true);

            mongoTemplate.updateFirst(query, update, Post.class);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Post> getReportedPosts() {
        return postRepository.findByReported_postTrue();
    }

    public List<Comment> showCommentReported() {
        return postRepository.findReportedComments();
    }


    public List<Post> findByCommunity(String communityName) {
        return postRepository.findByCommunity(communityName);
    }

    public Post addCommentToPost(String postUsername, String postTimestamp, String commenterUsername, Comment comment) {

        comment.setReported(false);
        Post post = postRepository.findByUsernameAndTimestamp(postUsername, postTimestamp);
        Long postId = Long.parseLong(post.getId());
        String community = post.getCommunity();
        System.out.println(post);

        if(!communityNeo4jRepository.isAlreadyJoined(commenterUsername, community)){
            throw new IllegalArgumentException("User has not joined community: " + community);
        }

        if (post != null) {
            comment.setUser(commenterUsername);
            comment.setTimestamp(LocalDateTime.parse(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));


            if (post.getComment() == null) {
                post.setComment(new ArrayList<>());
            }
            post.getComment().add(comment);
            post.setNum_comment(post.getNum_comment() + 1);
            postNeo4jRepository.addCommentToPost(postId, comment.getTimestamp().toString(), commenterUsername);
            return postRepository.save(post);
        }

        throw new IllegalArgumentException("Post not found for username: " + postUsername + " and timestamp: " + postTimestamp);
    }

    public List<PostSummaryDto> findControversialPosts() {
        return postRepository.findTopReportedPostsByCommentCount();
    }

    private String generatePreview(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;

    }
}

