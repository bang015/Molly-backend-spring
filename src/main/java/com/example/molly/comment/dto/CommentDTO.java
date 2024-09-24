package com.example.molly.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.example.molly.comment.entity.Comment;
import com.example.molly.user.dto.UserDTO;

@Data
public class CommentDTO {
  private Long id;
  private String content;
  private Long commentId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private int subCommentsCount;
  private Long postId;
  private UserDTO user;

  public CommentDTO(Comment comment, int count) {
    this.id = comment.getId();
    this.content = comment.getContent();
    this.commentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
    this.createdAt = comment.getCreatedAt();
    this.updatedAt = comment.getUpdatedAt();
    this.subCommentsCount = count;
    this.postId = comment.getPost().getId();
    this.user = comment.getUser() != null ? new UserDTO(comment.getUser()) : null;
  }
}
