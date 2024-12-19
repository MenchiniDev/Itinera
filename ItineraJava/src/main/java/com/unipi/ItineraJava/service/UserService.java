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
    private UserRepository userRepository;

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
}
