package com.example.molly.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.molly.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByNickname(String nickname);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.profileImage WHERE u.id NOT IN (SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) AND u.id <> :userId")
  List<User> findUserNotFollwedByUser(@Param("userId") Long userId, Pageable pageable);
}
