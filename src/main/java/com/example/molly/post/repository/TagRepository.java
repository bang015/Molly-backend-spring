package com.example.molly.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.example.molly.post.entity.Tag;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
  Optional<Tag> findByName(String name);

  @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword%")
  List<Tag> searchTagsByKeyword(String keyword);
}
