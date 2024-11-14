package com.example.molly.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.example.molly.comment.entity.Comment;
import com.example.molly.common.dto.BaseCountDTO;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByPostIdAndUserIdAndParentCommentIsNull(Long postId, Long userId);

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
  int countByParentCommentId(@Param("commentId") Long commentId);

  @Query(value = "SELECT c.parentComment.id as id, COUNT(c) as count " +
      "FROM Comment c " +
      "WHERE c.parentComment.id IN :commentIds " +
      "GROUP BY c.parentComment.id")
  List<BaseCountDTO> countByParentCommentIds(@Param("commentIds") List<Long> commentIds);

  @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.user.id <> :userId AND c.parentComment IS NULL")
  Page<Comment> findRootCommentsExcludingUserAndReplies(@Param("postId") Long postId, @Param("userId") Long userId,
      Pageable pageable);

  Page<Comment> findByParentComment(Comment parentComment, Pageable pageable);
}
