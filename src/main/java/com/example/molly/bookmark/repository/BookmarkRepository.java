package com.example.molly.bookmark.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.post.entity.Post;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  boolean existsByUserIdAndPostId(Long userId, Long postId);

  void deleteByUserIdAndPostId(Long userId, Long postId);

  @EntityGraph(attributePaths = { "post", "user" })
  @Query("SELECT b.post FROM Bookmark b WHERE b.user.id = :userId")
  Page<Post> findBookmarkedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
