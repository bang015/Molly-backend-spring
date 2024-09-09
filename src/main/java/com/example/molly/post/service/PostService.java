package com.example.molly.post.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponseDTO;
import com.example.molly.post.entity.Post;
import com.example.molly.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  private final FollowRepository followRepository;
  private final PostRepository postRepository;

  public PostResponseDTO getMainPost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIds(followedUserIds, pageable);
    List<PostDTO> postList = posts.stream().map(PostDTO::new).collect(Collectors.toList());
    return new PostResponseDTO(postList, posts.getTotalPages());
  }

  public PostResponseDTO getExplorePost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIdsNotIn(followedUserIds, pageable);
    List<PostDTO> postList = posts.stream().map(PostDTO::new).collect(Collectors.toList());
    return new PostResponseDTO(postList, posts.getTotalPages());
  }
}
