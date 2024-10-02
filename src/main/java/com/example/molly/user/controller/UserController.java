package com.example.molly.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.user.dto.UserCountsDTO;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.dto.UserResponse;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.ProfileImageService;
import com.example.molly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final ProfileImageService profileImageService;

  @PatchMapping()
  public ResponseEntity<?> updateUser(
      @RequestParam(required = false) String nickname,
      @RequestParam(required = false) String newPassword,
      @RequestParam(required = false) String currentPassword,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String introduce,
      @RequestParam(required = false) MultipartFile profileImage) {
    Long userId = SecurityUtil.getCurrentUserId();
    if (profileImage != null) {
      UserDTO user = profileImageService.updateProfileImage(userId, profileImage);
      return ResponseEntity.ok(new UserResponse(user, "프로필이 수정되었습니다."));
    }
    UserDTO user = userService.updateUserProfile(userId, name, nickname, introduce, newPassword, currentPassword);
    return ResponseEntity.ok(new UserResponse(user, "프로필이 수정되었습니다."));
  }

  // 내 정보
  @GetMapping("/me")
  public ResponseEntity<?> getMyInfo() {
    Long userId = SecurityUtil.getCurrentUserId();
    UserDTO userDto = userService.getUser(userId);
    return ResponseEntity.ok(userDto);
  }

  // 유저 정보
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
