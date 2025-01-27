package com.unipi.ItineraJava.controller;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.unipi.ItineraJava.DTO.*;

import com.unipi.ItineraJava.model.Last_post;
import com.unipi.ItineraJava.model.User;

import com.unipi.ItineraJava.repository.UserNeo4jRepository;
import com.unipi.ItineraJava.repository.UserRepository;
import com.unipi.ItineraJava.service.UserService;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;


@RestController
@RequestMapping("/users")
class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired UserNeo4jRepository userNeo4jRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // http://localhost:8080/users/signup
    //funzionante sabato
    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            System.out.println("Received signup request: " + signupRequest);
            System.out.println(signupRequest.getUsername());
            User user = new User();
            user.setId(UUID.randomUUID().toString()); // Genera un ID unico come stringa e non un tipo ObjectId
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setRole("USER");
            user.setCreated(LocalDateTime.now().toString());
            user.setActive(true);
            user.setReported(false);
            System.out.println(user.getUsername());
            System.out.println("Saving user: " + user);
            userRepository.save(user);

            userNeo4jRepository.createUserNode(user.getUsername());

            return ResponseEntity.ok("User registered successfully");
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // http://localhost:8080/users/signup/admin OK
    @Retryable(
            retryFor = TransactionSystemException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @PostMapping("/signup/admin")
    public ResponseEntity<String> signupAdmin(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString()); // Genera un ID unico come stringa e non un tipo ObjectId
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole("ADMIN");
        user.setCreated(LocalDateTime.now().toString());
        user.setActive(true);
        user.setReported(false);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // http://localhost:8080/users/login OK
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username");
        }

        //controllo campo active
        boolean isActive = userService.isUserActive(user.getUsername());
        if (!isActive) {
            return ResponseEntity.status(403).body("User account is inactive. You no longer have access to this application.");
        }

        if(passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            String token = JwtTokenProvider.generateToken(user.getUsername());
            return ResponseEntity.ok(token);
        }else
            return ResponseEntity.status(401).body("Invalid password");
    }

    // http://localhost:8080/users OK
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            return ResponseEntity.ok(userRepository.findAll());
        }else
        {
            return ResponseEntity.status(401).body(null);
        }
    }


    // http://localhost:8080/users/678f461050e5455936170332
    // delete an user
    /*
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token,
                                            @PathVariable String id) {
        try {
            if(User.isAdmin(token)) {
                userService.deleteById(id);
                return ResponseEntity.ok("user deleted");
            }else{
                return ResponseEntity.status(400).body("User not authenticated as Admin");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    // OK
    @PutMapping("/ban/{username}")
    public ResponseEntity<String> deactivateUser(@RequestHeader("Authorization") String token,
                                                 @PathVariable String username) {
        // Controlla se il token appartiene a un amministratore
        if (!User.isAdmin(token)) {
            return ResponseEntity.status(403).body("Unauthorized: Only admins can ban users");
        }

        try {
            // Aggiorna lo stato del campo active a false
            userService.deactivateUser(username);
            return ResponseEntity.ok("User banned successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("User not found: " + username);
        }
    }


    // PROFILE QUERIES
    /*
    @GetMapping("/profile/numpost")
    public long getPostCount(@RequestHeader("Authorization") String token)
    {
        try {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if (username == null)
                return ResponseEntity.status(401).body("Invalid token").getStatusCodeValue();
            return ResponseEntity.ok(UserService.getPostCount(username)).getBody();
        }catch (Exception e)
        {
            return ResponseEntity.status(400).body("Invalid Reponse").getStatusCodeValue();
        }
    }

    @GetMapping("/profile/numcom")
    public long getCommentCount(@RequestHeader("Authorization") String token)
    {
        try {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if (username == null)
                return ResponseEntity.status(401).body("Invalid token").getStatusCodeValue();
            return ResponseEntity.ok(UserService.getCommentCount(username)).getBody();
        }catch (Exception e)
        {
            return ResponseEntity.status(400).body("Invalid Reponse").getStatusCodeValue();
        }
    }*/
    // http://localhost:8080/users/profile/numreview
    // working fine
    @GetMapping("/profile/numreview")
    public String getReviewCount(@RequestHeader("Authorization") String token)
    {
        try {
            String username = JwtTokenProvider.getUsernameFromToken(token);
            if (username == null)
                return ResponseEntity.status(401).body("Invalid token").getBody();
            return ResponseEntity.ok("Num review for user " + username + " is " + UserService.getNumReview(username)).getBody();
        }catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Invalid Reponse").getBody();
        }
    }
    // http://localhost:8080/users/find/test
    //funzionante sabato
    @GetMapping("/find/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("Authenticated user: " + authentication.getName());
            return userService.findByUsername(username);
        }
        throw new AccessDeniedException("User not authenticated");
    }


    // Endpoint per aggiornare il campo "reported" per uno specifico user
    //funzionante sabato
    @PutMapping("/report/{username}")
    public ResponseEntity<String> reportUser(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Verifica se l'utente è autenticato
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User not authenticated. Please log in to access this endpoint.");
        }
        userService.updateReportedByUsername(username, true);
        return ResponseEntity.status(HttpStatus.OK)
                .body("User correctly reported.");

    }


    // returns all users reported
    @GetMapping("/reported")
    public ResponseEntity<List<ReportedUserDTO>> getReportedUsers(@RequestHeader("Authorization") String token) {
        if (User.isAdmin(token)) {
            List<ReportedUserDTO> reportedUsers = userService.getReportedUsers();
            if (reportedUsers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(reportedUsers);
        }else{
            return ResponseEntity.status(401).body(null);
        }
    }

    // returns the Last Post of a user
    @GetMapping("/lastpost/{username}")
    public ResponseEntity<Last_post> getLastPost(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
        try {
            Last_post last_post = userService.getLastPostByUsername(username);
            if (last_post == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(null);
            }

            return ResponseEntity.ok(last_post);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        }
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUserByUsername(
            @RequestHeader("Authorization") String token,
            @PathVariable String username) {
        if(User.isAdmin(token)) {
            if (userService.deleteByUsername(username))
                return ResponseEntity.ok("User deleted successfully");
            else
                return ResponseEntity.status(404).body("User not found");
        }else {
            return ResponseEntity.badRequest().body("User not authenticated. Please log in as an admin to access this endpoint.");
        }

    }



    // returns the communities joined by every single user
    // http://localhost:8080/users/profile/communityJoined
    @GetMapping("/profile/communityJoined")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCommunityJoined() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }
        String username = authentication.getName();

        try {
            List<CommunityDTO> communities = userService.getCommunityJoined(username);
            if (communities.isEmpty()) {
                return ResponseEntity.ok("No communities joined by the user.");
            }
            return ResponseEntity.ok(communities);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the communities: " + ex.getMessage());
        }
    }

    // returns the communities joined by a given user
    @GetMapping("/profile/communityJoined/{username}")
    public ResponseEntity<?> getCommunityJoined(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User not authenticated. Please log in to access this endpoint.");
            }
        try {
            List<CommunityDTO> communities = userService.getCommunityJoined(username);
            if (communities.isEmpty()) {
                return ResponseEntity.ok("No communities joined by the user.");
            }
            return ResponseEntity.ok(communities);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the communities: " + ex.getMessage());
        }
    }

    // returns top 10 most active users, based on posts and comments
    @GetMapping("/profile/mostactiveuser")
    public ResponseEntity<List<ActiveUserDTO>> getActiveUser(@RequestHeader("Authorization") String token) {
        if(User.isAdmin(token)) {
            return ResponseEntity.ok(userService.findTopActiveUsers());
        }else {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    

    // follows a user
    // http://localhost:8080/users/follow/{username}
    @PostMapping("/follow/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> followUser(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String follower = authentication.getName();
            try {
                userService.followUser(follower, username);
                return ResponseEntity.ok("User " + follower + " successfully followed user: " + username);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
    }
    
    // stops following a user
    //http://localhost:8080/users/unfollow/{username}
    @DeleteMapping("/unfollow/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> unfollowUser(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String follower = authentication.getName();
            try {
                userService.unfollowUser(follower, username);
                return ResponseEntity.ok("User " + follower + " successfully unfollowed user: " + username);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
    }


    // shows the people followed by every user
    @GetMapping("/showFollowing")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFollowing() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }
        String username = authentication.getName();
        try {
            List<UserDTO> following = userService.getFollowing(username);
            if (following.isEmpty()) {
                return ResponseEntity.ok("No users followed by the user.");
            }
            return ResponseEntity.ok(following);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the followed users: " + ex.getMessage());
        }
    }

    // shows given a user all the people he follows
    @GetMapping("/showFollowing/{username}")
    public ResponseEntity<?> getFollowing(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }
        try {
            List<UserDTO> following = userService.getFollowing(username);
            if (following.isEmpty()) {
                return ResponseEntity.ok("No users followed by the user.");
            }
            return ResponseEntity.ok(following);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving the followed users: " + ex.getMessage());
        }
    }

    // returns the people you may know based on your followings
    @GetMapping("/profile/peopleYouMayKnow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPeopleYouMayKnow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }

        String username = authentication.getName();
        try {
            List<String> suggestedUsernames = userService.getSuggestedUsernames(username);
            if (suggestedUsernames.isEmpty()) {
                return ResponseEntity.ok("No suggested users found.");
            }
            return ResponseEntity.ok(suggestedUsernames);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving suggested users: " + ex.getMessage());
        }
    }

    // returns all the recommended communities to join
    @GetMapping("/profile/recommendedCommunities")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSuggestedCommunities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }

        String username = authentication.getName();
        try {
            List<String> suggestedCommunities = userService.getSuggestedCommunities(username);
            if (suggestedCommunities.isEmpty()) {
                return ResponseEntity.ok("No suggested communities found.");
            }
            return ResponseEntity.ok(suggestedCommunities);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving suggested communities: " + ex.getMessage());
        }
    }
    
    // shows the posts you might lose
    @GetMapping("/profile/recommendedPosts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSuggestedPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("User not authenticated. Please log in to access this endpoint.");
        }

        String username = authentication.getName();
        try {
            List<PostSuggestionDto> suggestedPosts = userService.getSuggestedPosts(username);
            if (suggestedPosts.isEmpty()) {
                return ResponseEntity.ok("No suggested posts found.");
            }
            return ResponseEntity.ok(suggestedPosts);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving suggested posts: " + ex.getMessage());
        }
    }

}