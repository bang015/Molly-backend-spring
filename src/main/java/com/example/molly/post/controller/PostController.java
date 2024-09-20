package com.example.molly.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.molly.common.util.SecurityUtil;
import com.example.molly.post.dto.PostResponseDTO;
import com.example.molly.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @GetMapping("/main")
  public ResponseEntity<?> getMainPosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostResponseDTO result = postService.getMainPost(userId, page, limit);
    System.out.println(result);
    return ResponseEntity.ok(result);
  }

  @GetMapping
  public ResponseEntity<?> getExplorePosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostResponseDTO result = postService.getExplorePost(userId, page, limit);
    return ResponseEntity.ok(result);
  }

}
