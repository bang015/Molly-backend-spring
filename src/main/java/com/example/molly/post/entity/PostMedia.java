package com.example.molly.post.entity;

import com.example.molly.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostMedia extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "postId", nullable = false)
  private Post post;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String path;
}
