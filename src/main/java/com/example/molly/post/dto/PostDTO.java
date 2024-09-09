package com.example.molly.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.molly.post.entity.Post;
import com.example.molly.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class PostDTO {
  private long id;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private UserResponseDTO user;
  private List<PostMediaDTO> postMedias;

  public PostDTO(Post post) {
    this.id = post.getId();
    this.content = post.getContent();
    this.createdAt = post.getCreatedAt();
    this.updatedAt = post.getUpdatedAt();
    this.user = post.getUser() != null ? new UserResponseDTO(post.getUser()) : null;
    this.postMedias = post.getPostMedias().stream().map(PostMediaDTO::new).collect(Collectors.toList());
  }
}
