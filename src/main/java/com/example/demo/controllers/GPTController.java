package com.example.demo.controllers;

import com.example.demo.models.ChatThread;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.services.ChatThreadService;
import com.example.demo.services.GPTService;
import com.example.demo.services.MessageService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chat")
public class GPTController {

    @Autowired
    private GPTService gptService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatThreadService chatThreadService;

    @Autowired
    private UserService userService;

    @PostMapping("/ask")
    public String ask(@RequestParam Long threadId, @RequestParam String question, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getByUsername(username);

        // Πάρε το thread μόνο αν ανήκει στον χρήστη
        ChatThread thread = chatThreadService.getThreadByIdAndUser(threadId, user);

        // Αποθήκευση μηνύματος χρήστη
        Message userMsg = messageService.saveMessage(thread, "user", question, LocalDateTime.now());

        // Λήψη απάντησης από GPT
        String answer = gptService.askGPT(question).block();

        // Αποθήκευση απάντησης από GPT
        messageService.saveMessage(thread, "gpt", answer, LocalDateTime.now());

        return answer;
    }
}