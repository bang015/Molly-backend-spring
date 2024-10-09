package com.example.molly.chat.entity;

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
@Table(name = "`MessageReadStatus`")
public class MessageReadStatus extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "messageId", nullable = false)
  private ChatMessage message;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private boolean isRead = false;
}
