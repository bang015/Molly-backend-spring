package com.example.molly.post.dto;

import com.example.molly.post.entity.PostMedia;

import lombok.Data;

@Data
public class PostMediaDTO {
  private String path;

  public PostMediaDTO (PostMedia postMedia) {
    this.path = postMedia.getPath();
  }
}
