package com.example.molly.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.common.util.SecurityUtil;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponse;
import com.example.molly.post.dto.TagPaginationResponse;
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

  // 게시물 생성
  @PostMapping()
  public ResponseEntity<?> post(@RequestParam String content,
      @RequestParam MultipartFile[] postMedias,
      @RequestParam(required = false) List<String> hashtags) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostResponse postResponse = postService.post(postMedias, content,
        hashtags != null ? hashtags : new ArrayList<String>(), userId);
    return ResponseEntity.ok(postResponse);
  }

  // 게시물 수정
  @PatchMapping()
  public ResponseEntity<?> updatePost(@RequestBody UpdatePostRequest request) {
    Long userId = SecurityUtil.getCurrentUserId();
    PostResponse postResponse = postService.updatePost(request, userId);
    return ResponseEntity.ok(postResponse);
  }

  // 게시물 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable Long id) {
    Long userId = SecurityUtil.getCurrentUserId();
    postService.deletePost(id, userId);
    Map<String, Object> response = new HashMap<>();
    response.put("postId", id);
    response.put("message", "게시물이 삭제 되었습니다.");
    return ResponseEntity.ok(response);
  }

  // 메인 게시물 리스트
  @GetMapping("/main")
  public ResponseEntity<?> getMainPosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<PostDTO> postListResponse = postService.getMainPost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  // 추천 게시물 리스트
  @GetMapping
  public ResponseEntity<?> getExplorePosts(@RequestParam int page, @RequestParam(defaultValue = "5") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<PostDTO> postListResponse = postService.getExplorePost(userId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  // 유저 게시물 리스트
  @GetMapping("/my/{targetUserId}")
  public ResponseEntity<?> getUserPosts(@PathVariable Long targetUserId, @RequestParam int page,
      @RequestParam(defaultValue = "12") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    System.out.println(targetUserId);
    PaginationResponse<PostDTO> postListResponse = postService.getUserPost(userId, targetUserId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  // 북마크 게시물 리스트
  @GetMapping("/bookmark/{targetUserId}")
  public ResponseEntity<?> getBookmarkPosts(@PathVariable Long targetUserId, @RequestParam int page,
      @RequestParam(defaultValue = "12") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<PostDTO> postListResponse = postService.getBookmarkPost(userId, targetUserId, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

  // 태그 게시물 리스트
  @GetMapping("/tags/{tagName}")
  public ResponseEntity<?> getTagPosts(@PathVariable String tagName, @RequestParam int page,
      @RequestParam(defaultValue = "20") int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    TagPaginationResponse<PostDTO> postListResponse = postService.getTagPost(userId, tagName, page, limit);
    return ResponseEntity.ok(postListResponse);
  }

}
