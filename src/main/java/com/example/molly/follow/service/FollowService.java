package com.example.molly.follow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.follow.dto.FollowResponseDTO;
import com.example.molly.follow.entity.Follow;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;

  // 팔로우/ 언팔로우
  public boolean follow(Long userId, Long targetUserId) {
    User follower = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    User following = userRepository.findById(targetUserId)
        .orElseThrow(() -> new RuntimeException("Target user not found"));
    Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
    if (followOptional.isPresent()) {
      followRepository.delete(followOptional.get());
      return false;
    } else {
      Follow follow = new Follow();
      follow.setFollower(follower);
      follow.setFollowing(following);
      followRepository.save(follow);
      return true;
    }
  }

  // 추천 팔로우 리스트
  public List<FollowResponseDTO> getSuggestFollowers(Long userId, int limit) {
    Pageable pageable = PageRequest.of(0, limit);
    List<User> suggestFollowers = userRepository.findUserNotFollwedByUser(userId, pageable);
    List<Long> followersIds = followRepository.findFollowingUserIds(userId);
    List<FollowResponseDTO> suggestFollowerUsers = new ArrayList<>();
    for (User user : suggestFollowers) {
      String message = followersIds.contains(user.getId()) ? "회원님을 팔로우중입니다" : "회원님을 위한 추천";
      suggestFollowerUsers.add(new FollowResponseDTO(user, message, false));
    }
    return suggestFollowerUsers;
  }

  // 팔로윙 리스트
  public Map<String, Object> getFollowings(Long userId, String query, int page) {
    Pageable pageable = PageRequest.of(page - 1, 12);
    Page<Follow> followPage = followRepository.findFollowingsByUserIdAndQuery(userId, query, pageable);
    List<Long> followingUserIds = followPage.stream()
        .map(follow -> follow.getFollowing().getId())
        .collect(Collectors.toList());
    Long currentUserId = SecurityUtil.getCurrentUserId();
    List<Follow> followedUsers = followRepository.findFollowedUsers(currentUserId, followingUserIds);
    Set<Long> followedUserIds = followedUsers.stream()
        .map(follow -> follow.getFollowing().getId())
        .collect(Collectors.toSet());
    List<FollowResponseDTO> followings = followPage.stream().map(follow -> {
      return new FollowResponseDTO(follow.getFollowing(), "", followedUserIds.contains(follow.getFollowing().getId()));
    }).collect(Collectors.toList());
    HashMap<String, Object> result = new HashMap<>();
    result.put("followings", followings);
    result.put("totalPages", followPage.getTotalPages());
    System.out.println(result);
    return result;
  }
}
