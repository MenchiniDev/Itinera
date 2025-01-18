package com.unipi.ItineraJava.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Initializing Security Filter Chain...");

        http
                // Disabilitazione del CSRF per test
                .csrf(csrf -> {
                    log.info("Disabling CSRF protection...");
                    csrf.disable();
                })

                // Configurazione delle autorizzazioni per gli endpoint
                .authorizeHttpRequests(auth -> {
                    log.info("Configuring authorization rules...");
                    auth
                            .requestMatchers("/users/login", "/users/signup", "/users/signup/admin", "/users/login/admin")
                            .permitAll() // Consentire accesso libero a questi endpoint
                            .anyRequest()
                            .authenticated(); // Tutti gli altri richiedono autenticazione
                });

        log.info("Security Filter Chain configuration complete.");
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

