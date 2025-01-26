package com.unipi.ItineraJava.controller;


import com.unipi.ItineraJava.DTO.*;
import com.unipi.ItineraJava.model.Comment;
import com.unipi.ItineraJava.model.Post;
import com.unipi.ItineraJava.model.User;
import com.unipi.ItineraJava.service.CommunityService;
import com.unipi.ItineraJava.service.PostService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
class PostController {
    @Autowired
    private PostService postService;

    // adds a post in a community
    // http://localhost:8080/posts/Rome OK
    // bono il ramen ao
    @PostMapping("/{community}")
    public ResponseEntity<String> createPost(@RequestHeader("Authorization") String token,
                                                     @PathVariable String community,
                                                     @RequestBody String post) {
        if (!User.isAdmin(token)) {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(400).body("Invalid token");
            }
            if (post == null)
                return ResponseEntity.status(400).body("Invalid text");
            if (postService.addPost(community, username, post))
                return ResponseEntity.ok("post added successfully");
            else
                return ResponseEntity.badRequest().body("Error");
        }
        return ResponseEntity.status(400).body("Unauthorized");
    }

 
    // http://localhost:8080/posts/1a18dcfb-d748-4d53-bfe3-db3c21508498 OK
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String token,
                           @PathVariable String id) {
        if (User.isAdmin(token)){
            postService.deleteById(id);
            return ResponseEntity.ok("Post deleted");
        }
        else return ResponseEntity.internalServerError().body("Unauthorized");
    }

    // http://localhost:8080/posts/report OK
    @PutMapping("/report/{postId}")
    public ResponseEntity<String> reportPost(@RequestHeader("Authorization") String token,
                                             @PathVariable String postId ) {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        try {
            if (postService.reportPost(postId)) {
                return ResponseEntity.ok("Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error parsing timestamp");
        }

        return ResponseEntity.internalServerError().body("Error");
    }

    // http://localhost:8080/posts/comment ok
   
    @DeleteMapping("/comment/{commentID}")
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String token,
                                                @PathVariable String commentID) {
        if(User.isAdmin(token)){
            postService.updatePostAfterCommentRemoval(commentID);
            return ResponseEntity.ok("Comment deleted");
        }else
            return ResponseEntity.internalServerError().body("Unauthorized");
    }


    // http://localhost:8080/posts/report ok
    @GetMapping("/report")
    public ResponseEntity<List<Post>> showPostReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(postService.getReportedPosts());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // http://localhost:8080/posts/comment/report ok
    @PutMapping("/comment/report/{postId}/{commentId}")
    public ResponseEntity<String> reportComment(@RequestHeader("Authorization") String token,
                                             @PathVariable String postId,
                                             @PathVariable String  commentId)
    {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null)
            return ResponseEntity.badRequest().body("invalid token");
        if (postService.reportComment(postId,commentId))
            return ResponseEntity.ok("success");
        return ResponseEntity.internalServerError().body("error");
    }

    //non va ancora, ritorna
    //[
    //    {
    //        "username": null,
    //        "timestamp": null,
    //        "body": null,
    //        "reported": false
    //    }
    //]
    @GetMapping("/comment/report")
    public ResponseEntity<List<Comment>> showCommentReported(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(postService.showCommentReported());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //http://localhost:8080/posts/comment/Wooden-Secret5698 ok
    //  "comment": "mesi mesi ankara mesi, immenso mesi"
    @PostMapping("/comment/{postId}")
    public ResponseEntity<String> addCommentToPost(
            @RequestHeader("Authorization") String token,
            @PathVariable String postId, // of the post replying
            @RequestBody String comment) { // community timestamp comment textPost
        try {
            String commenterUsername = JwtTokenProvider.getUsernameFromToken(token);
            System.out.println(commenterUsername);
            if (commenterUsername == null)
                return ResponseEntity.internalServerError().body("token invalid");

            Post updatedPost = postService.addCommentToPost(commenterUsername,postId, comment);

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
    // http://localhost:8080/posts/profile ok
    // RETURNS THE POST NOT REPORTED WITH THE MOST NUMBER OF COMMENTS
    @GetMapping("viralposts")
    public ResponseEntity<List<PostSummaryDto>> findControversialPosts(@RequestHeader("Authorization") String token)
    {
        if(User.isAdmin(token))
            return ResponseEntity.ok(postService.findControversialPosts());
        else return ResponseEntity.badRequest().body(null);
    }


}
