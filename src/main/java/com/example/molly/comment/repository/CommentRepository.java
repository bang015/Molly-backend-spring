package com.example.molly.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.example.molly.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByPostIdAndUserIdAndParentCommentIsNull(Long postId, Long userId);

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
  int countByParentCommentId(@Param("commentId") Long commentId);

  @EntityGraph(attributePaths = { "user", "post" })
  @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.user.id <> :userId AND c.parentComment IS NULL")
  Page<Comment> findRootCommentsExcludingUserAndReplies(@Param("postId") Long postId, @Param("userId") Long userId,
      Pageable pageable);
}
