package com.example.molly.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.molly.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  void deleteByUserIdAndPostId(Long userId, Long postId);
}
