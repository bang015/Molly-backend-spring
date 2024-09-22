package com.example.molly.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponseDTO;
import com.example.molly.post.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @PostMapping()
  public ResponseEntity<?> post(@RequestParam String content,
      @RequestParam MultipartFile[] postMedias,
      @RequestParam(required = false) List<String> hashtags) {
    Long userId = SecurityUtil.getCurrentUserId();
    System.out.println(hashtags);
    PostDTO post = postService.post(postMedias, content, hashtags != null ? hashtags : new ArrayList<String>(), userId);
    Map<String, Object> response = new HashMap<>();
    response.put("post", post);
    response.put("message", "게시물이 공유 되었습니다.");
    return ResponseEntity.ok(response);
  }

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
