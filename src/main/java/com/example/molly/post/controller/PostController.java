package com.example.molly.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.post.dto.PostListResponse;
import com.example.molly.post.dto.PostResponse;
import com.example.molly.post.dto.UpdatePostRequest;
import com.example.molly.post.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    PostResponse postResponse = postService.post(postMedias, content,
        hashtags != null ? hashtags : new ArrayList<String>(), userId);
    return ResponseEntity.ok(postResponse);
  }

  @PatchMapping()
  public ResponseEntity<?> updatePost(@RequestBody UpdatePostRequest request) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostResponse postResponse = postService.updatePost(request, userId);
    return ResponseEntity.ok(postResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    Long userId = SecurityUtil.getCurrentUserId();
    postService.deletePost(id, userId);
    Map<String, Object> response = new HashMap<>();
    response.put("postId", id);
    response.put("message", "게시물이 삭제 되었습니다.");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/main")
  public ResponseEntity<?> getMainPosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostListResponse postListResponse = postService.getMainPost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  @GetMapping
  public ResponseEntity<?> getExplorePosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostListResponse postListResponse = postService.getExplorePost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  @GetMapping("/my/{userId}")
  public ResponseEntity<?> getUserPosts(@PathVariable Long userId, @RequestParam int page,
      @RequestParam(defaultValue = "12") int limit) {
    PostListResponse postListResponse = postService.getUserPost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  @GetMapping("/bookmark/{userId}")
  public ResponseEntity<?> getBookmarkPosts(@PathVariable Long userId, @RequestParam int page,
      @RequestParam(defaultValue = "12") int limit) {
    PostListResponse postListResponse = postService.getBookmarkPost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

}
