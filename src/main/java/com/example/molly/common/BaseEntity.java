package com.example.molly.common;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@MappedSuperclass
public abstract class BaseEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(updatable = false)
  private LocalDateTime createAt;

  private LocalDateTime updateAt;

  @PrePersist
  protected void onCreate() {
    this.createAt = LocalDateTime.now();
    this.updateAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updateAt = LocalDateTime.now();
  }
}
