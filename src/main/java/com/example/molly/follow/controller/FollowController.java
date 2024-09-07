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

  @GetMapping("/following")
  public ResponseEntity<?> selectFollowing(@RequestParam("userId") Long targetUserId,
      @RequestParam int page,
      @RequestParam("query") String keyword) {
    Map<String, Object> result = followService.getFollowings(targetUserId, keyword, page);
    return ResponseEntity.ok(result);
  }

}
