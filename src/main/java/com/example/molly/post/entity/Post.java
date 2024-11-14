package com.example.molly.post.entity;

import java.util.List;

import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;

import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.comment.entity.Comment;
import com.example.molly.common.BaseEntity;
import com.example.molly.like.entity.Like;
import com.example.molly.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@Table(name = "`Post`")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties("post")
  private List<Bookmark> bookmarks = new ArrayList<>();

  @Builder.Default
  @BatchSize(size = 12)
  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties("post")
  private List<PostMedia> postMedias = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties("post")
  private List<Comment> comments = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties("post")
  private List<Like> likedByUsers = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnoreProperties("post")
  private List<PostTag> postTags = new ArrayList<>();

  public void updateContent(String content) {
    this.content = content;
  }
}
