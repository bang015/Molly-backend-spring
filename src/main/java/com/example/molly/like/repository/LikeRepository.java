package com.example.molly.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  void deleteByUserIdAndPostId(Long userId, Long postId);

  long countByPostId(Long postId);
}
