package com.example.demo.repository;

import com.example.demo.models.ChatThread;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    List<ChatThread> findByUser(User user);
    Optional<ChatThread> findByIdAndUser(Long id, User user);
}