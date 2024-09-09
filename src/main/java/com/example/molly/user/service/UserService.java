package com.example.molly.user.service;

import org.springframework.stereotype.Service;

import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.dto.UserResponseDTO;
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

  public UserResponseDTO getUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾지 못했습니다."));
    UserResponseDTO userDto = new UserResponseDTO(user);
    long postCount = postRepository.countByUser(user);
    long followerCount = followRepository.countByFollower(user);
    long followingCount = followRepository.countByFollowing(user);
    userDto.setPostCount(postCount);
    userDto.setFollowerCount(followerCount);
    userDto.setFollowingCount(followingCount);
    return userDto;
  }

}
