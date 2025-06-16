package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    @JsonBackReference  // Αποφεύγει το infinite recursion στο JSON
    private ChatThread thread;

    private String sender;

    @Column(length = 5000)
    private String content;

    private LocalDateTime timestamp;

    public Message() {}

    public Message(ChatThread thread, String sender, String content, LocalDateTime timestamp) {
        this.thread = thread;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ChatThread getThread() {
        return thread;
    }
    public void setThread(ChatThread thread) {
        this.thread = thread;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
