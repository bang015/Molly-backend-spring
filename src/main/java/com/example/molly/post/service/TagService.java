package com.example.molly.post.service;

import org.springframework.stereotype.Service;
import com.example.molly.post.entity.Tag;
import com.example.molly.post.repository.PostTagRepository;
import com.example.molly.post.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;

  public Tag findOrCreateTag(String tagName) {
    Tag existingTag = tagRepository.findByName(tagName);
    if (existingTag != null) {
      return existingTag;
    }
    Tag newTag = Tag.builder().name(tagName).build();
    return tagRepository.save(newTag);
  }

  public void deleteUnusedTags(List<Tag> tags) {
    for (Tag tag : tags) {
      long count = postTagRepository.countByTag(tag);
      if (count == 0) {
        // 사용되지 않는 태그 삭제
        tagRepository.delete(tag);
      }
    }
  }
}
