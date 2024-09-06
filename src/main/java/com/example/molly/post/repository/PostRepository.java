package com.example.molly.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.post.entity.Post;
import com.example.molly.user.entity.User;

public interface PostRepository extends JpaRepository<Post, Long>{
  long countByUser(User user);
} 
