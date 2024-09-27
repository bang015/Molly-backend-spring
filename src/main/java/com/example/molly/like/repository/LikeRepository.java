package com.example.molly.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.like.entity.Like;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  List<Like> findByUserIdAndPostIdIn(Long userId, List<Long> postIds);

  void deleteByUserIdAndPostId(Long userId, Long postId);

  long countByPostId(Long postId);
}
