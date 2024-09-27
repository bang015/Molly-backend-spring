package com.example.molly.search.dto;

import com.example.molly.post.entity.Tag;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagSearchDTO {
  private Long id;
  private String name;
  private Long tagCount;
  private String type = "tag";

  public TagSearchDTO(Tag tag, Long tagCount) {
    this.id = tag.getId();
    this.name = tag.getName();
    this.tagCount = tagCount;
  }
}
