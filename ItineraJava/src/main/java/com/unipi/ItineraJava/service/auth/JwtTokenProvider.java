package com.unipi.ItineraJava.service.auth;

import com.unipi.ItineraJava.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import com.unipi.ItineraJava.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

    private final UserService userService;

    private static final String secretKey = "secretsecretsecretsecretsecretsecretsecretsecret"; // Cambia questo con una chiave segreta sicura
    private static final long validityInMilliseconds = 3600000 * 5; // 5 hour

    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    public static String generateToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT");
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        // Estrai il nome utente dal token
        String username = getUsernameFromToken(token);

        // Recupera le autorità dell'utente (es. ruolo)
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            // Creiamo un'istanza di UserDetails con le informazioni dell'utente (ruolo, etc.)
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    AuthorityUtils.createAuthorityList(user.get().getRole().toString()) // Usa il ruolo per le authorities
            );

            // Restituisce un'istanza di Authentication (è autenticato)
            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }


    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();  // Restituisce il nome utente
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Token non valido o scaduto");
        }
    }
}
