package com.example.molly.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.example.molly.comment.entity.Comment;
import com.example.molly.user.dto.UserResponseDTO;

@Data
public class CommentDTO {
  private Long id;
  private String content;
  private Long commentId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private int subCommentsCount;
  private Long postId;
  private UserResponseDTO user;

  public CommentDTO(Comment comment, int count) {
    this.id = comment.getId();
    this.commentId = comment.getParentComment().getId();
    this.createdAt = comment.getCreatedAt();
    this.updatedAt = comment.getUpdatedAt();
    this.subCommentsCount = count;
    this.postId = comment.getPost().getId();
    this.user = comment.getUser() != null ? new UserResponseDTO(comment.getUser()) : null;
  }
}
