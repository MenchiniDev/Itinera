package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public void deleteById(String id) {
        postRepository.deleteById(id);
    }

    public boolean reportPost(String timestamp, String user, String community) {
        try {
            Post post = postRepository.findPostByTimestampAndUsernameAndCommunity_name(timestamp, user, community);
            post.setReported_post(true);
            postRepository.save(post);
            return true;
        }catch (Exception e) {
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
}

