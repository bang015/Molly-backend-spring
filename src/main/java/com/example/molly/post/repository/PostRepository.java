package com.example.molly.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.post.entity.Post;
import com.example.molly.user.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {
  long countByUser(User user);

  @EntityGraph(attributePaths = { "postMedias", "user" })
  @Query("SELECT p FROM Post p WHERE p.user.id IN :userIds ORDER BY p.createdAt DESC")
  Page<Post> findPostsByUserIds(@Param("userIds") List<Long> userIds, Pageable pageable);

  @EntityGraph(attributePaths = { "postMedias", "user" })
  @Query("SELECT p FROM Post p WHERE p.user.id NOT IN :excludedUserIds ORDER BY p.createdAt DESC")
  Page<Post> findPostsByUserIdsNotIn(@Param("excludedUserIds") List<Long> excludedUserIds, Pageable pageable);
}
