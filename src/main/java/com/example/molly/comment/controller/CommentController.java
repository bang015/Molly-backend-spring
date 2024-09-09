package com.example.molly.comment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.comment.dto.CommentDTO;
import com.example.molly.comment.service.CommentService;
import com.example.molly.common.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/my")
  public ResponseEntity<?> getMyComment(@RequestParam Long postId) {
    System.out.println(postId);
    Long userId = SecurityUtil.getCurrentUserId();
    List<CommentDTO> comments = commentService.getMyComment(userId, postId);
    return ResponseEntity.ok(comments);
  }

}
