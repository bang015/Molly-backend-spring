package com.example.molly.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.comment.dto.CommentDTO;
import com.example.molly.comment.dto.CommentRequest;
import com.example.molly.comment.entity.Comment;
import com.example.molly.comment.repository.CommentRepository;
import com.example.molly.common.dto.BaseCountDTO;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.post.entity.Post;
import com.example.molly.post.service.PostService;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final UserService userService;
  private final PostService postService;
  private final CommentRepository commentRepository;

  // 댓글 생성
  @Transactional
  public CommentDTO createComment(Long userId, CommentRequest comment) {
    try {
      User user = userService.getUser(userId);
      Post post = postService.getPost(comment.getPostId());
      Comment parentComment = null;
      // 대댓글 여부 확인
      if (comment.getCommentId() != null) {
        parentComment = commentRepository.findById(comment.getCommentId())
            .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
      }
      // 댓글 생성
      Comment newComment = Comment.builder().post(post).user(user).parentComment(parentComment)
          .content(comment.getContent()).build();
      commentRepository.save(newComment);
      return new CommentDTO(newComment, 0);
    } catch (Exception e) {
      throw new RuntimeException("댓글 생성 중 오류가 발생했습니다.", e);
    }
  }

  // 댓글 수정
  @Transactional
  public CommentDTO updateComment(Long userId, Long commentId, String content) {
    try {
      // 댓글 존재여부, 권한 확인
      Comment comment = verifyCommentUser(commentId, userId);
      comment.updateContent(content);
      return new CommentDTO(comment, getSubCommentCount(commentId));
    } catch (Exception e) {
      throw new RuntimeException("댓글 수정 중 오류가 발생했습니다.", e);
    }
  }

  // 댓글 삭제
  @Transactional
  public void deleteComment(Long userId, Long commentId) {
    try {
      Comment comment = verifyCommentUser(commentId, userId);
      commentRepository.delete(comment);
    } catch (Exception e) {
      throw new RuntimeException("댓글 삭제 중 오류가 발생했습니다.", e);
    }
  }

  // 대댓글 카운트
  public int getSubCommentCount(Long commentId) {
    return commentRepository.countByParentCommentId(commentId);
  }

  // 댓글 리스트
  public PaginationResponse<CommentDTO> getComment(Long userId, Long postId, int page) {
    int limit = 15;
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Comment> commentPage = commentRepository.findRootCommentsExcludingUserAndReplies(postId, userId, pageable);
    List<CommentDTO> commentList = getCommentDTOList(commentPage);
    return new PaginationResponse<CommentDTO>(commentList, commentPage.getTotalPages());
  }

  // 대댓글 리스트
  public List<CommentDTO> getSubComment(Long commentId, int page) {
    int limit = 3;
    Pageable pageable = PageRequest.of(page - 1, limit);
    Comment parentComment = commentRepository.findById(commentId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
    Page<Comment> subCommentsPage = commentRepository.findByParentComment(parentComment, pageable);
    return getCommentDTOList(subCommentsPage);
  }

  // 내가 작성한 댓글 리스트
  public List<CommentDTO> getMyComment(Long userId, Long postId) {
    List<Comment> comments = commentRepository.findByPostIdAndUserIdAndParentCommentIsNull(postId, userId);
    List<Long> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
    List<BaseCountDTO> counts = commentRepository.countByParentCommentIds(commentIds);
    Map<Long, Long> countMap = counts.stream().collect(Collectors.toMap(BaseCountDTO::getId, BaseCountDTO::getCount));
    return comments.stream().map(comment -> {
      long count = countMap.getOrDefault(comment.getId(), 0L);
      return new CommentDTO(comment, count);
    }).collect(Collectors.toList());
  }

  // CommentDTO로 포맷
  List<CommentDTO> getCommentDTOList(Page<Comment> commentPage) {
    List<Long> commentIds = commentPage.stream().map(Comment::getId).collect(Collectors.toList());
    // 댓글들의 대댓글 수
    List<BaseCountDTO> counts = commentRepository.countByParentCommentIds(commentIds);
    Map<Long, Long> countMap = counts.stream().collect(Collectors.toMap(BaseCountDTO::getId, BaseCountDTO::getCount));
    return commentPage.stream().map(comment -> {
      // 해당 댓글과 대댓글 수 매치
      long count = countMap.getOrDefault(comment.getId(), 0L);
      return new CommentDTO(comment, count);
    }).collect(Collectors.toList());
  }

  // 댓글 존재 여부와 권한 확인
  Comment verifyCommentUser(Long commentId, Long userId) {
    Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
    if (!comment.getUser().getId().equals(userId)) {
      new RuntimeException("권한이 없습니다.");
    }
    return comment;
  }
}