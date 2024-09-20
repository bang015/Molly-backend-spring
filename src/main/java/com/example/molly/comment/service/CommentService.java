package com.example.molly.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.molly.comment.dto.CommentDTO;
import com.example.molly.comment.dto.CommentResponse;
import com.example.molly.comment.entity.Comment;
import com.example.molly.comment.repository.CommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;

  public int getSubCommentCount(Long commentId) {
    return commentRepository.countByParentCommentId(commentId);
  }

  public CommentResponse getComment(Long userId, Long postId, int page) {
    int limit = 15;
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Comment> commentPage = commentRepository.findRootCommentsExcludingUserAndReplies(postId, userId, pageable);
    List<CommentDTO> commentList = getCommentDTOList(commentPage);
    return new CommentResponse(commentList, commentPage.getTotalPages());
  }

  public List<CommentDTO> getMyComment(Long userId, Long postId) {
    List<Comment> comments = commentRepository.findByPostIdAndUserIdAndParentCommentIsNull(postId, userId);
    return comments.stream().map(comment -> {
      int count = getSubCommentCount(comment.getId());
      return new CommentDTO(comment, count);
    }).collect(Collectors.toList());
  }

  List<CommentDTO> getCommentDTOList(Page<Comment> commentPage) {
    return commentPage.stream().map(comment -> {
      int count = getSubCommentCount(comment.getId());
      return new CommentDTO(comment, count);
    }).collect(Collectors.toList());
  }
}