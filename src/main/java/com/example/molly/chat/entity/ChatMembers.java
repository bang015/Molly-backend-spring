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
@Table(name = "`ChatMembers`")
public class ChatMembers extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "roomId", nullable = false)
  private ChatRoom room;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  @Builder.Default
  private boolean isActive = false;

  public void changeIsActive(boolean isActive){
    this.isActive = isActive;
  }
}
