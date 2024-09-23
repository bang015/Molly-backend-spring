package com.example.molly.post.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UpdatePostRequest {
  private String content;
  private Long postId;
  private List<String> hashtags = new ArrayList<>();
}
