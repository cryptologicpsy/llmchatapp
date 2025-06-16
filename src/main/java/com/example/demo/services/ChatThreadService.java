package com.example.demo.services;

import com.example.demo.models.ChatThread;
import com.example.demo.models.User;
import com.example.demo.repository.ChatThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatThreadService {

    @Autowired
    private ChatThreadRepository chatThreadRepository;

    // Βρες ένα thread με βάση το id και τον χρήστη (για ασφάλεια)
    public ChatThread getThreadByIdAndUser(Long id, User user) {
        return chatThreadRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Thread not found with id " + id + " for user " + user.getUsername()));
    }

    // Δημιουργία νέου thread με συσχέτιση στον χρήστη
    public ChatThread createThread(String title, User user) {
        ChatThread thread = new ChatThread();
        thread.setTitle(title);
        thread.setUser(user);
        return chatThreadRepository.save(thread);
    }

    // Λήψη όλων των threads του συγκεκριμένου χρήστη
    public List<ChatThread> getThreadsByUser(User user) {
        return chatThreadRepository.findByUser(user);
    }
}