package com.unipi.ItineraJava.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

// POST
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String communityId;
    private String user;
    private LocalDateTime timestamp;
    private int numComment;
    private boolean reported;
    private List<Comment> commenti;

    // Getters and Setters
}

// COMMENT
class Comment {
    private String user;
    private LocalDateTime timestamp;
    private String text;
    private boolean reported;

    // Getters and Setters
}
