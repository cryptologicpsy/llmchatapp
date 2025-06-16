package com.example.demo.services;

import com.example.demo.models.ChatThread;
import com.example.demo.models.Message;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(ChatThread thread, String sender, String content, LocalDateTime timestamp) {
        Message message = new Message(thread, sender, content, timestamp);
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByThreadId(Long threadId) {
        return messageRepository.findByThreadId(threadId);
    }
}
