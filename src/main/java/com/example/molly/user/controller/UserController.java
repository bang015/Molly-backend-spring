package com.example.molly.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  // 유저 정보
  @GetMapping("/me")
  public void getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println(authentication);
    // return ResponseEntity.ok(authentication);
  }

}
