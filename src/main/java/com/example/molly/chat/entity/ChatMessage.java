package com.example.molly.chat.entity;

import java.util.List;

import com.example.molly.common.BaseEntity;
import com.example.molly.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "roomId", nullable = false)
  private ChatRoom room;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageReadStatus> readStatuses;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "ENUM('USER', 'SYSTEM') DEFAULT 'USER'")
  @Builder.Default
  private MessageType type = MessageType.USER;
}
