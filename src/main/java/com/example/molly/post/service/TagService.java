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

  // 태그 찾거나 생성
  public Tag findOrCreateTag(String tagName) {
    return tagRepository.findByName(tagName)
        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
  }

  // 사용하지 않는 태그 삭제
  public void deleteUnusedTags(List<Tag> tags) {
    for (Tag tag : tags) {
      long count = postTagRepository.countByTag(tag);
      if (count == 0) {
        tagRepository.delete(tag);
      }
    }
  }
}
