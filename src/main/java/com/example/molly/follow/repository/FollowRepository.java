package com.example.molly.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.molly.follow.entity.Follow;
import com.example.molly.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
  long countByFollower(User user);

  long countByFollowing(User user);

  List<Follow> findByFollowerId(Long followerId);

  Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
