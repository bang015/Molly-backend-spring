package com.example.molly.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.molly.comment.dto.CommentDTO;
import com.example.molly.comment.dto.CommentRequest;
import com.example.molly.comment.dto.CommentResponse;
import com.example.molly.comment.entity.Comment;
import com.example.molly.comment.repository.CommentRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final UserRepository userRepository;
  private final PostRepository postRepository;
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

  public CommentDTO createComment(Long userId, CommentRequest comment) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));
    Comment parentComment = null;
    if (comment.getCommentId() != null) {
      parentComment = commentRepository.findById(comment.getCommentId())
          .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
    }
    Comment newComment = Comment.builder().post(post).user(user).parentComment(parentComment)
        .content(comment.getContent()).build();
    commentRepository.save(newComment);
    return new CommentDTO(newComment, 0);
  }

  List<CommentDTO> getCommentDTOList(Page<Comment> commentPage) {
    return commentPage.stream().map(comment -> {
      int count = getSubCommentCount(comment.getId());
      return new CommentDTO(comment, count);
    }).collect(Collectors.toList());
  }
}