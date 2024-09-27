package com.example.molly.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.chat.entity.ChatMessage;
import com.example.molly.chat.entity.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  
  Optional<ChatMessage> findTopByRoomOrderByCreatedAtDesc(ChatRoom room);
}
