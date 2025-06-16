package com.example.demo.controllers;

import com.example.demo.models.ChatThread;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.services.ChatThreadService;
import com.example.demo.services.MessageService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatThreadService chatThreadService;

    @Autowired
    private UserService userService;

    @PostMapping("/send")
    public Message sendMessage(@RequestParam Long threadId,
                               @RequestParam String sender,
                               @RequestParam String content,
                               Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);

        // Πάρε το thread μόνο αν ανήκει στον χρήστη
        ChatThread thread = chatThreadService.getThreadByIdAndUser(threadId, user);

        return messageService.saveMessage(thread, sender, content, LocalDateTime.now());
    }

    @GetMapping("/byThread/{threadId}")
    public List<Message> getMessagesByThread(@PathVariable Long threadId, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);

        // Πάρε το thread μόνο αν ανήκει στον χρήστη (για ασφαλή πρόσβαση)
        ChatThread thread = chatThreadService.getThreadByIdAndUser(threadId, user);

        return messageService.getMessagesByThreadId(thread.getId());
    }
}
