package com.example.molly.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.user.dto.UserResponseDTO;
import com.example.molly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  // 유저 정보
  @GetMapping("/me")
  public ResponseEntity<?> getUser() {
    Long userId = SecurityUtil.getCurrentUserId();
    UserResponseDTO userDto = userService.getUser(userId);
    return ResponseEntity.ok(userDto);
  }
}
