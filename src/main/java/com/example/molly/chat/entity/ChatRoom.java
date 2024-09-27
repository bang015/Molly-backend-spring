package com.example.molly.chat.entity;

import java.util.List;

import com.example.molly.common.BaseEntity;
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
public class ChatRoom extends BaseEntity {
  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatMembers> users;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatMessage> messages;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private boolean isGroupChat = false;
}
