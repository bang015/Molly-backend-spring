package com.example.molly.post.service;

import org.springframework.stereotype.Service;
import com.example.molly.post.entity.Tag;
import com.example.molly.post.repository.TagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  public Tag findOrCreateTag(String tagName) {
    Tag existingTag = tagRepository.findByName(tagName);
    if (existingTag != null) {
      return existingTag;
    }
    Tag newTag = Tag.builder().name(tagName).build();
    return tagRepository.save(newTag);
  }
}
