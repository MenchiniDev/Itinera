package com.unipi.ItineraJava.configuration;

import com.unipi.ItineraJava.service.auth.JwtAuthenticationFilter;
import com.unipi.ItineraJava.service.auth.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Initializing Security Filter Chain...");

        // Disabilita CSRF per test
        http.csrf(csrf -> {
            log.info("Disabling CSRF protection...");
            csrf.disable();
        });

        // Configurazione delle autorizzazioni per gli endpoint
        http.authorizeHttpRequests(auth -> {
            log.info("Configuring authorization rules...");
            auth
                    .requestMatchers("/v3/api-docs", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/users/login", "/users/signup", "/users/signup/admin", "/users/login/admin")
                    .permitAll() // Consentire accesso libero a questi endpoint
                    .anyRequest()
                    .authenticated(); // Tutti gli altri richiedono autenticazione
        });

        // Aggiungi il filtro JWT alla catena di filtri di Spring Security
        log.info("Adding JWT Authentication Filter...");
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        log.info("Security Filter Chain configuration complete.");
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
