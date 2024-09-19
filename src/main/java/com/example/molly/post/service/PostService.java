package com.example.molly.post.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.molly.bookmark.repository.BookmarkRepository;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.like.repository.LikeRepository;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponseDTO;
import com.example.molly.post.entity.Post;
import com.example.molly.post.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  private final FollowRepository followRepository;
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final BookmarkRepository bookmarkRepository;

  @Transactional
  public PostResponseDTO getMainPost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIds(followedUserIds, pageable);
    List<PostDTO> postList = posts.stream().map(post -> {
      boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
      long likeCount = likeRepository.countByPostId(post.getId());
      boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
      return new PostDTO(post, isLiked, likeCount, isBookmarked);
    }).collect(Collectors.toList());
    return new PostResponseDTO(postList, posts.getTotalPages());
  }

  @Transactional
  public PostResponseDTO getExplorePost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIdsNotIn(followedUserIds, pageable);
    List<PostDTO> postList = posts.stream().map(post -> {
      boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
      long likeCount = likeRepository.countByPostId(post.getId());
      boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
      return new PostDTO(post, isLiked, likeCount, isBookmarked);
    }).collect(Collectors.toList());
    return new PostResponseDTO(postList, posts.getTotalPages());
  }
}
