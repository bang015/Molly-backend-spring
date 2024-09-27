package com.example.molly.chat.dto;

import java.time.LocalDateTime;

import com.example.molly.chat.entity.ChatMessage;
import com.example.molly.user.dto.UserDTO;

import lombok.Data;

@Data
public class ChatMessageDTO {
  private Long id;
  private UserDTO user;
  private String message;
  private LocalDateTime createdAt;

  public ChatMessageDTO(ChatMessage message) {
    this.id = message.getId();
    this.user = message.getUser() != null ? new UserDTO(message.getUser()) : null;
    this.message = message.getMessage();
    this.createdAt = message.getCreatedAt();
  }
}
