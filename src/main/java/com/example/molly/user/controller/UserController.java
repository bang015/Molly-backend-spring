package com.example.molly.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.user.dto.UserCountsDTO;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  // 유저 정보
  @GetMapping("/me")
  public ResponseEntity<?> getMyInfo() {
    Long userId = SecurityUtil.getCurrentUserId();
    UserDTO userDto = userService.getUser(userId);
    return ResponseEntity.ok(userDto);
  }

  @GetMapping()
  public ResponseEntity<?> getUserProfile(@RequestParam String nickname) {
    User user = userService.findUserByNickname(nickname);
    UserCountsDTO counts = userService.calculateUserCounts(user);
    UserDTO userDTO = new UserDTO(user);
    userDTO.setPostCount(counts.getPostCount());
    userDTO.setFollowerCount(counts.getFollowerCount());
    userDTO.setFollowingCount(counts.getFollowingCount());
    return ResponseEntity.ok(userDTO);
  }

}
