package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.DTO.PostSummaryDto;
import com.unipi.ItineraJava.DTO.ReportCommentRequest;
import com.unipi.ItineraJava.DTO.ReportPostRequest;
import com.unipi.ItineraJava.DTO.commentDTO;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.CommunityService;
import com.unipi.ItineraJava.service.PostService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommunityService communityService;

    // http://localhost:8080/posts
    // returns all posts of a community
    @GetMapping("/{communityName}")
    public List<Post> getAllPosts(@PathVariable String communityName) {
        if(communityName==null || communityName.isEmpty() || !communityService.existsCommunity(communityName)) {
            return null;
        }else {
            return postService.findByCommunity(communityName);
        }
    }

    // http://localhost:8080/posts/678e56991a6dfb7aa1fbc30a
    // todo: indebuggabile
    /*
    @GetMapping("/{id}")
    public Optional<Post> getPostById(@PathVariable String id) {
        return postService.getPostById(id);
    }*/

    // working
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String token,
                           @PathVariable String id) {
        if (User.isAdmin(token)){
            postService.deleteById(id);
            return ResponseEntity.ok("Post deleted");
        }
        else return ResponseEntity.internalServerError().body("Unauthorized");
    }


    //forse ok
    @DeleteMapping("/comment")
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String token,
                                                @RequestBody String text) {
        if(User.isAdmin(token)){
            postService.deleteByText(text);
            return ResponseEntity.ok("Comment deleted");
        }else
            return ResponseEntity.internalServerError().body("Unauthorized");
    }


    // http://localhost:8080/posts/report
    // working
    @PutMapping("/report")
    public ResponseEntity<String> reportPost(@RequestHeader("Authorization") String token,
                                             @RequestBody ReportPostRequest request) {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        try {
            System.out.println("BODY ARRIVATO:" + request.getBody());
            if (postService.reportPost(request.getBody(), request.getUser(), request.getCommunity())) {
                return ResponseEntity.ok("Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error parsing timestamp");
        }

        return ResponseEntity.internalServerError().body("Error");
    }


    // http://localhost:8080/posts/report
    // working
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
                                             @RequestBody ReportCommentRequest report)
    {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null)
            return ResponseEntity.badRequest().body("invalid token");
        if (postService.reportComment(report.getCommunity(),report.getUser(),report.getTextComment()))
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

    //aggiunge un commento
    @PostMapping("/comment/{username}")
    public ResponseEntity<String> addCommentToPost(
            @RequestHeader("Authorization") String token,
            @PathVariable String username, // of the post replying
            @RequestBody commentDTO commentDTO) {
        try {
            String commenterUsername = JwtTokenProvider.getUsernameFromToken(token);
            System.out.println(commenterUsername);
            if (commenterUsername == null)
                return ResponseEntity.internalServerError().body("token invalid");

            Comment comment = new Comment();
            comment.setUser(commenterUsername);
            comment.setTimestamp(String.valueOf(LocalDateTime.now()));
            comment.setText(commentDTO.getComment());
            comment.setReported(false);

            Post updatedPost = postService.addCommentToPost(username, commentDTO.getTimestamp(), commenterUsername, comment);

            if (updatedPost != null) {
                return ResponseEntity.ok("Commento aggiunto");
            }

            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("profile")
    public ResponseEntity<List<PostSummaryDto>> findControversialPosts(@RequestHeader("Authorization") String token)
    {
        if(User.isAdmin(token))
            return ResponseEntity.ok(postService.findControversialPosts());
        else return ResponseEntity.badRequest().body(null);
    }


}
