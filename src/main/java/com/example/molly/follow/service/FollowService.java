package com.example.molly.follow.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.follow.dto.FollowResponse;
import com.example.molly.follow.entity.Follow;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final UserService userService;
  private final FollowRepository followRepository;

  // 팔로우 / 언팔로우
  @Transactional
  public boolean follow(Long userId, Long targetUserId) {
    User follower = userService.getUser(userId);
    User following = userService.getUser(targetUserId);
    // 타겟유저와의 팔로우 상태를 확인
    Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
    if (followOptional.isPresent()) {
      // 팔로우되어있는 상태라면 팔로우 삭제
      followRepository.delete(followOptional.get());
      return false;
    } else {
      // 아니라면 팔로우 추가
      Follow follow = Follow.builder().follower(follower).following(following).build();
      followRepository.save(follow);
      return true;
    }
  }

  // 팔로우 상태 확인
  public boolean isFollowed(Long userId, Long targetUserId) {
    User follower = userService.getUser(userId);
    User following = userService.getUser(targetUserId);
    Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
    if (followOptional.isPresent()) {
      return true;
    } else {
      return false;
    }
  }

  // 추천 팔로우 리스트
  public List<FollowResponse> getSuggestFollowers(Long userId, int limit) {
    Pageable pageable = PageRequest.of(0, limit);
    List<FollowResponse> suggestions = new ArrayList<>();
    Set<Long> addedUserIds = new HashSet<>();
    System.out.println("limit:" + limit);
    // 1. 나를 팔로우 중인 유저 가져오기
    List<FollowResponse> followers = followRepository.findNonMutualFollowers(userId, pageable);
    for (FollowResponse follower : followers) {
      if (addedUserIds.add(follower.getId())) {
        suggestions.add(follower);
      }
    }
    System.out.println("suggestions:" + suggestions);
    // 2. 팔로워 수가 많은 유저 가져오기 (나를 팔로우 중인 유저가 이미 10명 이상일 경우 생략)
    if (suggestions.size() < limit) {
      List<FollowResponse> popularUsers = followRepository.findPopularUsers(userId, pageable);
      System.out.println("popularUsers:" + popularUsers);

      for (FollowResponse user : popularUsers) {
        if (suggestions.size() >= limit)
          break;
        if (addedUserIds.add(user.getId())) {
          suggestions.add(user);
        }
      }
    }

    return suggestions;
  }

  // 팔로윙 리스트
  public PaginationResponse<FollowResponse> getFollowings(Long userId, Long targetUserId, String query, int page) {
    Pageable pageable = PageRequest.of(page - 1, 12);
    // 유저의 팔로윙 리스트를 페이지네이션하여 가져옴
    // 검색어가 있다면 닉네임 또는 이름에 검색어가 포함된 유저를 찾음
    Page<Follow> followPage = followRepository.findFollowingsByUserIdAndQuery(targetUserId, query, pageable);
    List<Long> followingUserIds = followPage.stream()
        .map(follow -> follow.getFollowing().getId())
        .collect(Collectors.toList());
    List<FollowResponse> followings = getFollowResponseList(userId, followingUserIds, true, followPage);
    return new PaginationResponse<FollowResponse>(followings, page);
  }

  // 팔로워 리스트
  public PaginationResponse<FollowResponse> getFollowers(Long userId, Long targetUserId, String query, int page) {
    Pageable pageable = PageRequest.of(page - 1, 12);
    // 유저의 팔로워 리스트를 페이지네이션하여 가져옴
    // 검색어가 있다면 닉네임 또는 이름에 검색어가 포함된 유저를 찾음
    Page<Follow> followPage = followRepository.findFollowersByUserIdAndQuery(targetUserId, query, pageable);
    List<Long> followerUserIds = followPage.stream()
        .map(follow -> follow.getFollower().getId())
        .collect(Collectors.toList());
    List<FollowResponse> followers = getFollowResponseList(userId, followerUserIds, false, followPage);
    return new PaginationResponse<FollowResponse>(followers, page);
  }

  // FollowResponse 포맷
  List<FollowResponse> getFollowResponseList(Long userId, List<Long> followUserIds, boolean isFollowing,
      Page<Follow> followPage) {
    // 해당 유저의 팔로워 또는 팔로윙들을 '내'가 팔로우했는지 체크
    Set<Long> followedUserIds = followRepository.findFollowedUserIds(userId, followUserIds);
    return followPage.stream().map(follow -> {
      User followUser = isFollowing ? follow.getFollowing() : follow.getFollower();
      Long followUserId = isFollowing ? follow.getFollowing().getId() : follow.getFollower().getId();
      return new FollowResponse(followUser, "", followedUserIds.contains(followUserId));
    }).collect(Collectors.toList());
  }
}
