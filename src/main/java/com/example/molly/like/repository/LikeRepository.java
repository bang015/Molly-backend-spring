package com.example.molly.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.common.dto.BaseCountDTO;
import com.example.molly.like.entity.Like;
import java.util.List;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId AND l.post.id IN :postIds")
  Set<Long> findByUserIdAndPostIdIn(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);

  void deleteByUserIdAndPostId(Long userId, Long postId);

  long countByPostId(Long postId);

  @Query(value = "SELECT l.post.id as id, COUNT(l.post) as count FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
  List<BaseCountDTO> countByPostIds(@Param("postIds") List<Long> postIds);
}
