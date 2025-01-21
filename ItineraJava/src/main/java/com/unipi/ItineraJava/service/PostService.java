package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.DTO.PostDTO;
import com.unipi.ItineraJava.configuration.StringToLocalDateTimeConverter;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();

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
                        post.setTimestamp(postDTO.getTimestamp().toString());
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

    public boolean reportPost(String timestamp, String user, String community) {
        try {
            Optional<PostDTO> postDTO = postRepository.findPostByTimestampAndUsernameAndCommunity(timestamp, user, community);

            if (postDTO.isPresent()) {
                PostDTO dto = postDTO.get();

                Post post = new Post();
                post.setTimestamp(dto.getTimestamp());
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
}

