package com.example.molly.follow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.common.util.SecurityUtil;
import com.example.molly.follow.dto.FollowRequest;
import com.example.molly.follow.dto.FollowResponse;
import com.example.molly.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import java.util.List;
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

  // 팔로우 추가/해제
  @PostMapping
  public ResponseEntity<?> follow(@RequestBody FollowRequest request) {
    Long userId = SecurityUtil.getCurrentUserId();
    boolean isFollowed = followService.follow(userId, request.getFollowUserId());
    return ResponseEntity.ok(isFollowed);
  }

  // 추천 팔로우 유저 리스트
  @GetMapping
  public ResponseEntity<?> suggestFollowers(@RequestParam int limit) {
    Long userId = SecurityUtil.getCurrentUserId();
    List<FollowResponse> suggestFollowerList = followService.getSuggestFollowers(userId, limit);
    return ResponseEntity.ok(suggestFollowerList);
  }

  // 팔로우 상태 확인
  @GetMapping("/check/{targetUserId}")
  public ResponseEntity<?> isFollwed(@PathVariable Long targetUserId) {
    System.out.println(targetUserId);
    Long userId = SecurityUtil.getCurrentUserId();
    boolean isFollowed = followService.isFollowed(userId, targetUserId);
    System.out.println(isFollowed);
    return ResponseEntity.ok(isFollowed);
  }

  // 팔로윙 리스트
  @GetMapping("/following/{targetUserId}/")
  public ResponseEntity<?> selectFollowing(@PathVariable Long targetUserId,
      @RequestParam int page,
      @RequestParam("query") String keyword) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<FollowResponse> result = followService.getFollowings(userId, targetUserId, keyword, page);
    return ResponseEntity.ok(result);
  }

  // 팔로워 리스트
  @GetMapping("/follower/{targetUserId}/")
  public ResponseEntity<?> selectFollower(@PathVariable Long targetUserId,
      @RequestParam int page,
      @RequestParam("query") String keyword) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<FollowResponse> result = followService.getFollowers(userId, targetUserId, keyword, page);
    return ResponseEntity.ok(result);
  }
}
