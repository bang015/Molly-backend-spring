package com.example.molly.user.entity;

import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.molly.auth.dto.SignUpRequest;
import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.chat.entity.ChatMembers;
import com.example.molly.comment.entity.Comment;
import com.example.molly.common.BaseEntity;
import com.example.molly.follow.entity.Follow;
import com.example.molly.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseEntity {
  @Column(nullable = false, unique = true)
  @Email(message = "Invalid email format")
  private String email;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(columnDefinition = "TEXT")
  private String introduce;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "profileImageId")
  private ProfileImage profileImage;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Bookmark> bookmarks;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> following;

  @OneToMany(mappedBy = "following", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> followers;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatMembers> chatRooms;

  @ManyToMany(mappedBy = "likedByUsers", fetch = FetchType.LAZY)
  private List<Post> likedPosts;

  protected User() {
  }

  public User(SignUpRequest signUpRequest) {
    this.email = signUpRequest.getEmail();
    this.nickname = signUpRequest.getNickname();
    this.name = signUpRequest.getName();
    this.password = signUpRequest.getPassword();
  }

  private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

  @PrePersist
  @PreUpdate
  private void encryptPassword() {
    if (this.password != null && !this.password.isEmpty()) {
      this.password = encoder.encode(this.password);
    }
  }
}
