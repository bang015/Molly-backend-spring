package com.example.molly.post.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.entity.PostTag;
import com.example.molly.post.entity.Tag;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
  List<PostTag> findByPost(Post post);

  void deleteByPost(Post post);

  long countByTag(Tag tag);
}
