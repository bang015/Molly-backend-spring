package com.example.molly.bookmark.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.bookmark.service.BookmarkService;
import com.example.molly.common.util.SecurityUtil;
import com.example.molly.post.dto.PostIdDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
  private final BookmarkService bookmarkService;

  @PostMapping()
  public ResponseEntity<?> postBookmark(@RequestBody PostIdDTO postIdDTO) {
    Long userId = SecurityUtil.getCurrentUserId();
    boolean postBookmark = bookmarkService.togglePostBookmark(userId, postIdDTO.getPostId());
    return ResponseEntity.ok(postBookmark);
  }

}
