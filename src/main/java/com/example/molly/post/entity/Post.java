package com.example.molly.post.entity;

import java.util.List;

import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.comment.entity.Comment;
import com.example.molly.common.BaseEntity;
import com.example.molly.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bookmark> bookmarks;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostMedia> postMedias;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @ManyToMany
  @JoinTable(name = "PostTag", joinColumns = @JoinColumn(name = "postId"), inverseJoinColumns = @JoinColumn(name = "tagId"))
  private List<Tag> tags;

  @ManyToMany
  @JoinTable(name = "PostLike", joinColumns = @JoinColumn(name = "postId"), inverseJoinColumns = @JoinColumn(name = "userId"))
  private List<User> likedByUsers;
}
