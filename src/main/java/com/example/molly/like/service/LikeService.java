package com.example.molly.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.like.entity.Like;
import com.example.molly.like.repository.LikeRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Transactional
  public boolean togglePostLike(Long userId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

    boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, postId);

    if (isLiked) {
      likeRepository.deleteByUserIdAndPostId(userId, postId);
      return false;
    } else {
      Like postLike = new Like();
      postLike.setPost(post);
      postLike.setUser(user);
      likeRepository.save(postLike);
      return true;
    }
  }
}
