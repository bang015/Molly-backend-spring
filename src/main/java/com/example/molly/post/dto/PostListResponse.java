package com.example.molly.post.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostListResponse {
  private List<PostDTO> postList;
  private int totalPages;
  private Long tagPostCount;

  public PostListResponse(List<PostDTO> postList, int totalPages) {
    this.postList = postList;
    this.totalPages = totalPages;
  }

  public PostListResponse(List<PostDTO> postList, int totalPages, Long tagPostCount) {
    this.postList = postList;
    this.totalPages = totalPages;
    this.tagPostCount = tagPostCount;
  }
}
