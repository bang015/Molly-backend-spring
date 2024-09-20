package com.example.molly.like.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.like.service.LikeService;
import com.example.molly.post.dto.PostRequestDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
  private final LikeService likeService;

  @PostMapping()
  public ResponseEntity<?> likePost(@RequestBody PostRequestDTO postRequestDTO) {
    Long userId = SecurityUtil.getCurrentUserId();
    boolean likePost = likeService.togglePostLike(userId, postRequestDTO.getPostId());
    return ResponseEntity.ok(likePost);
  }

}
