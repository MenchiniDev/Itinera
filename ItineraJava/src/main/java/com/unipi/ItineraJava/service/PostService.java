package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PostService {
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
                        post.setTimestamp(postDTO.getTimestamp());
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
    }

    public boolean reportPost(String body, String user, String community) {
            try {
                Optional<Post> postDTO = postRepository.findPostByTimestampAndUsernameAndCommunity(body, user, community);
                if (postDTO.isPresent()) {
                    Post post = postDTO.get();
                    post.setReported_post(true);
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

        public boolean reportComment(String community, String user, String text) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("community").is(community));
            query.addCriteria(Criteria.where("comment.username").is(user));
            query.addCriteria(Criteria.where("comment.body").is(text));

            Update update = new Update();
            update.set("comment.$.reported", true);

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

    public Post addCommentToPost(String postUsername, String postTimestamp, String commenterUsername, Comment comment) {

        comment.setReported(false);
        Post post = postRepository.findByUsernameAndTimestamp(postUsername, postTimestamp);
        System.out.println(post);

        if (post != null) {
            comment.setUser(commenterUsername);
            comment.setTimestamp(LocalDateTime.parse(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));


            if (post.getComment() == null) {
                post.setComment(new ArrayList<>());
            }
            post.getComment().add(comment);
            post.setNum_comment(post.getNum_comment() + 1);
            return postRepository.save(post);
        }

        throw new IllegalArgumentException("Post not found for username: " + postUsername + " and timestamp: " + postTimestamp);
    }

    public List<PostSummaryDto> findControversialPosts() {
        return postRepository.findTopReportedPostsByCommentCount();
    }

    public void deleteByText(String text) {
        postRepository.deleteByText(text);
    }
}

