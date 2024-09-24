package com.example.molly.user.service;

import org.springframework.stereotype.Service;

import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.dto.UserCountsDTO;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final FollowRepository followRepository;

  public User findUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElse(null);
    return user;
  }

  public User findUserByNickname(String nickname) {
    User user = userRepository.findByNickname(nickname).orElse(null);
    return user;
  }

  public UserDTO getUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾지 못했습니다."));
    UserDTO userDto = new UserDTO(user);
    UserCountsDTO counts = calculateUserCounts(user);
    userDto.setPostCount(counts.getPostCount());
    userDto.setFollowerCount(counts.getFollowerCount());
    userDto.setFollowingCount(counts.getFollowingCount());
    return userDto;
  }

  public UserCountsDTO calculateUserCounts(User user) {
    Long postCount = postRepository.countByUser(user);
    Long followingCount = followRepository.countByFollower(user);
    Long followerCount = followRepository.countByFollowing(user);
    return new UserCountsDTO(postCount, followerCount, followingCount);
  }

}
