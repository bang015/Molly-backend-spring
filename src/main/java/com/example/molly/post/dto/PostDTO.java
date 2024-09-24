package com.example.molly.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.molly.post.entity.Post;
import com.example.molly.user.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PostDTO {
  private long id;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private UserDTO user;
  private List<PostMediaDTO> postMedias;
  @JsonProperty("isLiked")
  private boolean isLiked;
  private long likeCount;
  @JsonProperty("isBookmarked")
  private boolean isBookmarked;

  public PostDTO(Post post, boolean isLiked, long likeCount, boolean isBookmarked) {
    this.id = post.getId();
    this.content = post.getContent();
    this.createdAt = post.getCreatedAt();
    this.updatedAt = post.getUpdatedAt();
    this.user = post.getUser() != null ? new UserDTO(post.getUser()) : null;
    this.postMedias = post.getPostMedias().stream().map(PostMediaDTO::new).collect(Collectors.toList());
    this.isLiked = isLiked;
    this.likeCount = likeCount;
    this.isBookmarked = isBookmarked;
  }
}
