package com.example.molly.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.post.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Tag findByName(String name);
}
