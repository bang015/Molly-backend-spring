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
import com.example.molly.follow.dto.FollowResponseDTO;
import com.example.molly.follow.entity.Follow;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;

  // 팔로우/ 언팔로우
  @Transactional
  public boolean follow(Long userId, Long targetUserId) {
    User follower = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    User following = userRepository.findById(targetUserId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
    if (followOptional.isPresent()) {
      followRepository.delete(followOptional.get());
      return false;
    } else {
      Follow follow = Follow.builder().follower(follower).following(following).build();
      followRepository.save(follow);
      return true;
    }
  }

  public boolean isFollowed(Long userId, Long targetUserId) {
    User follower = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    User following = userRepository.findById(targetUserId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
    if (followOptional.isPresent()) {
      return false;
    } else {
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
  public Map<String, Object> getFollowings(Long userId, Long targetUserId, String query, int page) {
    Pageable pageable = PageRequest.of(page - 1, 12);
    Page<Follow> followPage = followRepository.findFollowingsByUserIdAndQuery(targetUserId, query, pageable);
    List<Long> followingUserIds = followPage.stream()
        .map(follow -> follow.getFollowing().getId())
        .collect(Collectors.toList());
    List<FollowResponseDTO> followings = getFollowResponseDTOList(userId, followingUserIds, true, followPage);
    HashMap<String, Object> result = new HashMap<>();
    result.put("followings", followings);
    result.put("totalPages", followPage.getTotalPages());
    return result;
  }

  public Map<String, Object> getFollowers(Long userId, Long targetUserId, String query, int page) {
    Pageable pageable = PageRequest.of(page - 1, 12);
    Page<Follow> followPage = followRepository.findFollowersByUserIdAndQuery(targetUserId, query, pageable);
    List<Long> followerUserIds = followPage.stream()
        .map(follow -> follow.getFollower().getId())
        .collect(Collectors.toList());
    List<FollowResponseDTO> followings = getFollowResponseDTOList(userId, followerUserIds, false, followPage);
    HashMap<String, Object> result = new HashMap<>();
    result.put("followers", followings);
    result.put("totalPages", followPage.getTotalPages());
    return result;
  }

  List<FollowResponseDTO> getFollowResponseDTOList(Long userId, List<Long> followUserIds, boolean isFollowing,
      Page<Follow> followPage) {
    List<Follow> followedUsers = followRepository.findFollowedUsers(userId, followUserIds);
    Set<Long> followedUserIds = followedUsers.stream()
        .map(follow -> follow.getFollowing().getId())
        .collect(Collectors.toSet());
    return followPage.stream().map(follow -> {
      User followUser = isFollowing ? follow.getFollowing() : follow.getFollower();
      Long followUserId = isFollowing ? follow.getFollowing().getId() : follow.getFollower().getId();
      return new FollowResponseDTO(followUser, "", followedUserIds.contains(followUserId));
    }).collect(Collectors.toList());
  }
}
