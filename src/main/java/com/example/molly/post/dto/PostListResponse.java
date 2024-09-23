package com.example.molly.post.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostListResponse {
  private List<PostDTO> postList;
  private int totalPages;

  public PostListResponse(List<PostDTO> postList, int totalPages) {
    this.postList = postList;
    this.totalPages = totalPages;
  }
}
