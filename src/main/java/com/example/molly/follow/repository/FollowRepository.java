package com.example.molly.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.follow.entity.Follow;
import com.example.molly.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    long countByFollower(User user);

    long countByFollowing(User user);

    // 해당 유저를 팔로우 했는지 체크
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
    List<Long> findFollowerUserIds(@Param("userId") Long userId);

    @Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :userId")
    List<Long> findFollowingUserIds(@Param("userId") Long userId);

    // 리스트의 유저들을 팔로우 했는지 체크
    @Query("SELECT f FROM Follow f WHERE f.follower.id = :currentUserId AND f.following.id IN :userIds")
    List<Follow> findFollowedUsers(@Param("currentUserId") Long currentUserId,
            @Param("userIds") List<Long> userIds);

    // 팔로윙 리스트
    @Query("SELECT f FROM Follow f " +
            "JOIN f.following u " +
            "WHERE f.follower.id = :userId " +
            "AND (u.name LIKE %:query% OR u.nickname LIKE %:query%)")
    Page<Follow> findFollowingsByUserIdAndQuery(@Param("userId") Long userId, @Param("query") String query,
            Pageable pageable);
}
