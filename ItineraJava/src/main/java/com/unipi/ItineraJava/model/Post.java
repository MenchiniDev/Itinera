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

    // request GET /communities?id=roma&username=john_doe
    public <U> U getTimestamp() {

        // query usata per ritornare il timestamp del post
        //
        // se l'utente non ha l'arco tra community e lui (ossia non è loggato)
        // allora si ritorna solo l'ultimo post con il timestamp
        // altrimenti si ritornano tutti i post e commenti
        return (U) timestamp; //todo: aggiustare con il timestamp post di riferimento
    }
}

// COMMENT
class Comment {
    private String user;
    private LocalDateTime timestamp;
    private String text;
    private boolean reported;

    // Getters and Setters
}
