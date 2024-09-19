package com.example.molly.like.entity;

import com.example.molly.common.BaseEntity;
import com.example.molly.post.entity.Post;
import com.example.molly.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "`Like`")
@Getter
public class Like extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "postId", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;
}
