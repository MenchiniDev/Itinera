package com.unipi.ItineraJava.service;


import com.unipi.ItineraJava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unipi.ItineraJava.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService{
    @Autowired
    private static UserRepository userRepository;

    @Autowired // necessario altrimenti non consente autenticazione ruolo con funzione User.isAdmin()
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<com.unipi.ItineraJava.model.User> findAll() {
        return userRepository.findAll();
    }

    public Optional<com.unipi.ItineraJava.model.User> findById(String id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    //modifiche bache
    // Trova un utente per username
    public static Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Aggiorna il campo "reported" per uno specifico username
    public void updateReportedByUsername(String username, boolean reported) {
        userRepository.updateReportedByUsername(username, reported);
    }

    public Object loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
