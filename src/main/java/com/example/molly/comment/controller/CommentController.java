package com.example.molly.comment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.comment.dto.CommentDTO;
import com.example.molly.comment.dto.CommentRequest;
import com.example.molly.comment.dto.CommentResponse;
import com.example.molly.comment.service.CommentService;
import com.example.molly.common.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/my")
  public ResponseEntity<?> getMyComment(@RequestParam Long postId) {
    Long userId = SecurityUtil.getCurrentUserId();
    List<CommentDTO> comments = commentService.getMyComment(userId, postId);
    return ResponseEntity.ok(comments);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<?> getComment(@PathVariable Long postId, @RequestParam int page) {
    Long userId = SecurityUtil.getCurrentUserId();
    CommentResponse comments = commentService.getComment(userId, postId, page);
    return ResponseEntity.ok(comments);
  }

  @PostMapping()
  public ResponseEntity<?> postComment(@RequestBody CommentRequest commentRequest) {
    Long userId = SecurityUtil.getCurrentUserId();
    CommentDTO newCommentDTO = commentService.createComment(userId, commentRequest);
    return ResponseEntity.ok(newCommentDTO);
  }

}
