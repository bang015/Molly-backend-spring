package com.example.molly.post.dto;

import java.util.List;

import com.example.molly.common.dto.PaginationResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TagPaginationResponse<T> extends PaginationResponse<T> {

  private Long tagPostCount;

  public TagPaginationResponse(List<T> result, int totalPages, Long tagPostCount) {
    super(result, totalPages);
    this.tagPostCount = tagPostCount;
  }
}
