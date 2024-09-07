package com.example.molly.post.dto;

import java.util.List;

import lombok.Data;

@Data
public class PostResponseDTO {
  private List<PostDTO> postList;
  private int totalPages;

  public PostResponseDTO(List<PostDTO> postList, int totalPages) {
    this.postList = postList;
    this.totalPages = totalPages;
  }
}
