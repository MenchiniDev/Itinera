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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private User user;

    @PostMapping("/{community}")
    public ResponseEntity<String> createPost(@RequestHeader("Authorization") String token,
                                                     @PathVariable String community,
                                                     @RequestBody String post) {
        String username = JwtTokenProvider.getUsernameFromToken(token);
        if(username == null) {
            return null;
        }
        if(postService.addPost(community, username, post))
            return ResponseEntity.ok("post added successfully");
        else
            return ResponseEntity.badRequest().body("Error");
    }

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

    // http://localhost:8080/posts/report
    // working
    // todo metti l'_id al posto del ReportPostRequest ok
    @PutMapping("/report/{postId}")
    public ResponseEntity<String> reportPost(@RequestHeader("Authorization") String token,
                                             @PathVariable String postId ) { //body, user, community
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

    // http://localhost:8080/posts/comment
    //working
    @DeleteMapping("/comment")
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String token,
                                                @RequestBody String text) {
        if(User.isAdmin(token)){
            postService.updatePostAfterCommentRemoval(text);
            return ResponseEntity.ok("Comment deleted");
        }else
            return ResponseEntity.internalServerError().body("Unauthorized");
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

    // http://localhost:8080/posts/comment/report
    // {
    //    "user":"ZenBoyNothingHead",
    //    "textComment":"Just moved into a new neighborhood and there's a community compost bin. I love the idea of composting so we can waste less, but there are no instructions or websites to find instructions on the bin. Anyone know how this works??",
    //    "community":"Amsterdam"
    //    "textPost": "bo"
    // }
    // working
    // todo: metti il Postid anzichè user e community ok
    @PutMapping("/comment/report/{postId}/{commentId}")
    public ResponseEntity<String> reportComment(@RequestHeader("Authorization") String token,
                                             @PathVariable String postId,
                                             @RequestBody String  commentId)
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

    //http://localhost:8080/posts/comment/Wooden-Secret5698
    //{
    //  "comment": "mesi mesi ankara mesi, immenso mesi"
    //}
    // todo: metti l'id al posto di community e user e timestamp ok
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
    // http://localhost:8080/posts/profile
    // ritorna il post reportato con il maggior numero di commenti sotto
    //[
    //    {
    //        "id": "679111e3bc7b0722300762be",
    //        "community": "Barcelona",
    //        "username": "Wooden-Secret5698",
    //        "post": "this scammer found me 🤦‍♂️",
    //        "reportedComments": 1,
    //        "timestamp": "2024-12-22T13:37:44Z"
    //    }
    //]
    //todo: migliora nome ok
    @GetMapping("viralposts")
    public ResponseEntity<List<PostSummaryDto>> findControversialPosts(@RequestHeader("Authorization") String token)
    {
        if(User.isAdmin(token))
            return ResponseEntity.ok(postService.findControversialPosts());
        else return ResponseEntity.badRequest().body(null);
    }


}
