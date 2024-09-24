package com.example.molly.follow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.follow.dto.FollowRequestDTO;
import com.example.molly.follow.dto.FollowResponseDTO;
import com.example.molly.follow.service.FollowService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
  private final FollowService followService;

  @PostMapping
  public ResponseEntity<?> follow(@RequestBody FollowRequestDTO request) {
    Long userId = SecurityUtil.getCurrentUserId();
    boolean isFollowed = followService.follow(userId, request.getFollowUserId());
    return ResponseEntity.ok(isFollowed);
  }

  @GetMapping
  public ResponseEntity<?> suggestFollowers(@RequestParam int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    List<FollowResponseDTO> suggestFollowerList = followService.getSuggestFollowers(userId, limit);
    return ResponseEntity.ok(suggestFollowerList);
  }

  @GetMapping("/check/{targetUserId}")
  public ResponseEntity<?> isFollwed(@PathVariable Long targetUserId) {
    System.out.println(targetUserId);
    Long userId = SecurityUtil.getCurrentUserId();
    boolean isFollowed = followService.isFollowed(userId, targetUserId);
    return ResponseEntity.ok(isFollowed);
  }

  @GetMapping("/following/{targetUserId}/")
  public ResponseEntity<?> selectFollowing(@PathVariable Long targetUserId,
      @RequestParam int page,
      @RequestParam("query") String keyword) {
    Long userId = SecurityUtil.getCurrentUserId();
    Map<String, Object> result = followService.getFollowings(userId, targetUserId, keyword, page);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/follower/{targetUserId}/")
  public ResponseEntity<?> selectFollower(@PathVariable Long targetUserId,
      @RequestParam int page,
      @RequestParam("query") String keyword) {
    Long userId = SecurityUtil.getCurrentUserId();
    Map<String, Object> result = followService.getFollowers(userId, targetUserId, keyword, page);
    return ResponseEntity.ok(result);
  }
}
