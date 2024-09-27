package com.example.molly.post.entity;

import java.util.List;

import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;

import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.comment.entity.Comment;
import com.example.molly.common.BaseEntity;
import com.example.molly.like.entity.Like;
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
public class Post extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bookmark> bookmarks = new ArrayList<>();

  @Builder.Default
  @BatchSize(size = 12)
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostMedia> postMedias = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Like> likedByUsers = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostTag> postTags = new ArrayList<>();

  public void updateContent(String content) {
    this.content = content;
  }
}
