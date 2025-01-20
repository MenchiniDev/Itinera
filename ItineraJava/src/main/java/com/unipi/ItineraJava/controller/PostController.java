package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.Review;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.PostService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Post> getPostById(@PathVariable String id) {
        return postService.findById(id);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.save(post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        postService.deleteById(id);
    }

    @PutMapping("/report")
    public ResponseEntity<String> reportPost(@RequestHeader("Authorization") String token,
                            @RequestBody String timestamp,
                            @RequestBody String user,
                            @RequestBody String community)
    {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null)
            return ResponseEntity.badRequest().body("invalid token");
        if (postService.reportPost(timestamp,user,community))
            return ResponseEntity.ok("success");
        return ResponseEntity.internalServerError().body("error");
    }

    @GetMapping("/report")
    public ResponseEntity<List<Post>> showPostReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(postService.getReportedPosts());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/comment/report")
    public ResponseEntity<String> reportComment(@RequestHeader("Authorization") String token,
                                             @RequestBody String timestamp,
                                             @RequestBody String user,
                                             @RequestBody String text)
    {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null)
            return ResponseEntity.badRequest().body("invalid token");
        if (postService.reportComment(timestamp,user,text))
            return ResponseEntity.ok("success");
        return ResponseEntity.internalServerError().body("error");
    }

    @GetMapping("/comment/report")
    public ResponseEntity<List<Comment>> showCommentReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(postService.showCommentReported());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


}
