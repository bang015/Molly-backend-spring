package com.example.molly.comment.dto;

import lombok.Data;

@Data
public class CommentRequest {
  private Long PostId;
  private Long CommentId;
  private String content;
}
