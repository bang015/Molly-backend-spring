package com.example.molly.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.example.molly.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByPostIdAndUserIdAndParentCommentIsNull(Long postId, Long userId);

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
  int countByParentCommentId(@Param("commentId") Long commentId);
}
