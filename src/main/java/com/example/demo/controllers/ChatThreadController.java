package com.example.demo.controllers;

import com.example.demo.models.ChatThread;
import com.example.demo.models.User;
import com.example.demo.services.ChatThreadService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/threads")
public class ChatThreadController {

    @Autowired
    private ChatThreadService chatThreadService;

    @Autowired
    private UserService userService;

    // Δημιουργία νέου thread για τον τρέχοντα user
    @PostMapping("/create")
    public ChatThread createThread(@RequestParam String title, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return chatThreadService.createThread(title, user);
    }

    // Λήψη συγκεκριμένου thread με id και έλεγχος ιδιοκτησίας
    @GetMapping("/{id}")
    public ChatThread getThreadById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return chatThreadService.getThreadByIdAndUser(id, user);
    }

    // Λήψη όλων των threads του τρέχοντος χρήστη
    @GetMapping("/all")
    public List<ChatThread> getAllThreads(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);
        return chatThreadService.getThreadsByUser(user);
    }
}