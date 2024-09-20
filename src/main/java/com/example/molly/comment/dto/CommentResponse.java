package com.example.molly.comment.dto;

import java.util.List;
import lombok.Data;

@Data
public class CommentResponse {
  private List<CommentDTO> commentList;
  private int totalPages;

  public CommentResponse(List<CommentDTO> commentList, int totalPages) {
    this.commentList = commentList;
    this.totalPages = totalPages;
  }
}
