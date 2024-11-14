package com.example.molly.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.like.entity.Like;
import com.example.molly.like.repository.LikeRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.service.PostService;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final PostService postService;
  private final UserService userService;

  @Transactional
  public boolean togglePostLike(Long userId, Long postId) {
    Post post = postService.getPost(postId);
    User user = userService.getUser(userId);
    // 좋아요 상태 확인
    boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);
    if (isLiked) {
      likeRepository.deleteByUserIdAndPostId(userId, postId);
      return false;
    } else {
      Like postLike = Like.builder().post(post).user(user).build();
      likeRepository.save(postLike);
      return true;
    }
  }
}
