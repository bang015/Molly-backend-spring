package com.example.molly.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.common.service.RedisService;
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
  private final PasswordEncoder passwordEncoder;
  private final RedisService redisService;

  // 이메일로 유저 찾기
  public User findUserByEmail(String email) {
    User user = userRepository.findByEmail(email).orElse(null);
    return user;
  }

  // 닉네임으로 유저 찾기
  public User findUserByNickname(String nickname) {
    User user = userRepository.findByNickname(nickname).orElse(null);
    return user;
  }

  // 유저 정보 캐싱
  public User getUser(Long userId) {
    User user = redisService.get("getUser-userId:" + userId, User.class);
    if (user == null) {
      user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾지 못했습니다."));
      redisService.save("getUser-userId:" + userId, user);
    }
    return user;
  }

  // 유저 DTO
  public UserDTO getUserDTO(Long userId) {
    User user = getUser(userId);
    UserDTO userDto = new UserDTO(user);
    UserCountsDTO counts = calculateUserCounts(user);
    userDto.setPostCount(counts.getPostCount());
    userDto.setFollowerCount(counts.getFollowerCount());
    userDto.setFollowingCount(counts.getFollowingCount());
    return userDto;
  }

  // 유저 게시물,팔로워,팔로윙 카운트
  public UserCountsDTO calculateUserCounts(User user) {
    Long postCount = postRepository.countByUser(user);
    Long followingCount = followRepository.countByFollower(user);
    Long followerCount = followRepository.countByFollowing(user);
    return new UserCountsDTO(postCount, followerCount, followingCount);
  }

  // 유저 정보 수정
  @Transactional
  public UserDTO updateUserProfile(Long userId, String name, String nickname, String introduce, String newPassword,
      String currentPassword) {
    try {
      User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾지 못했습니다."));
      user.updateProfile(name, nickname, introduce);
      if (newPassword != null && passwordEncoder.matches(currentPassword, user.getPassword())) {
        user.updatePassword(passwordEncoder.encode(newPassword));
      }
      redisService.delete("getUser-userId:" + userId);
      return new UserDTO(user);
    } catch (Exception e) {
      throw new RuntimeException("프로필 수정 중 오류가 발생했습니다.");
    }
  }
}
