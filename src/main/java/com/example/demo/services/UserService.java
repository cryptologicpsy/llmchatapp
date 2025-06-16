package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Επιστρέφει χρήστη ή πετάει exception αν δεν βρεθεί
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // Επιστρέφει Optional<User> αν χρειάζεσαι να ελέγχεις την ύπαρξη
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Αποθηκεύει νέο χρήστη ή ενημερώνει υπάρχοντα
    public User save(User user) {
        return userRepository.save(user);
    }

    // Επιστρέφει χρήστη με βάση το ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Ελέγχει αν υπάρχει χρήστης με το συγκεκριμένο username
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}