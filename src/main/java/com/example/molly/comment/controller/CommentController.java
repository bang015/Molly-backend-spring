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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  // 댓글 생성
  @PostMapping()
  public ResponseEntity<?> postComment(@RequestBody CommentRequest commentRequest) {
    Long userId = SecurityUtil.getCurrentUserId();
    CommentDTO newCommentDTO = commentService.createComment(userId, commentRequest);
    return ResponseEntity.ok(newCommentDTO);
  }

  // 댓글 수정
  @PatchMapping("/{id}")
  public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
    Long userId = SecurityUtil.getCurrentUserId();
    CommentDTO comment = commentService.updateComment(userId, id, commentRequest.getContent());
    return ResponseEntity.ok(comment);
  }

  // 댓글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable Long id) {
    Long userId = SecurityUtil.getCurrentUserId();
    commentService.deleteComment(userId, id);
    return ResponseEntity.ok(id);
  }

  // 내가 작성한 댓글 리스트
  @GetMapping("/my")
  public ResponseEntity<?> getMyComment(@RequestParam Long postId) {
    Long userId = SecurityUtil.getCurrentUserId();
    List<CommentDTO> comments = commentService.getMyComment(userId, postId);
    return ResponseEntity.ok(comments);
  }

  // 댓글 리스트
  @GetMapping("/{postId}")
  public ResponseEntity<?> getComment(@PathVariable Long postId, @RequestParam int page) {
    Long userId = SecurityUtil.getCurrentUserId();
    CommentResponse comments = commentService.getComment(userId, postId, page);
    return ResponseEntity.ok(comments);
  }

  // 대댓글 리스트
  @GetMapping("/sub/{id}")
  public ResponseEntity<?> getSubComment(@PathVariable Long id, @RequestParam int page) {
    List<CommentDTO> comments = commentService.getSubComment(id, page);
    return ResponseEntity.ok(comments);
  }

}
