package com.example.molly.like.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.like.service.LikeService;
import com.example.molly.post.dto.PostIdDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
  private final LikeService likeService;

  // 좋아요 추가/해제
  @PostMapping()
  public ResponseEntity<?> likePost(@RequestBody PostIdDTO postIdDTO) {
    Long userId = SecurityUtil.getCurrentUserId();
    boolean likePost = likeService.togglePostLike(userId, postIdDTO.getPostId());
    return ResponseEntity.ok(likePost);
  }

}
