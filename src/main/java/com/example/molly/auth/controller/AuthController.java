package com.example.molly.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.molly.auth.dto.JwtToken;
import com.example.molly.auth.dto.SendEmailRequest;
import com.example.molly.auth.dto.SignInRequest;
import com.example.molly.auth.dto.SignUpRequest;
import com.example.molly.auth.security.JwtTokenProvider;
import com.example.molly.auth.service.AuthService;
import com.example.molly.common.service.EmailService;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;
  private final JwtTokenProvider jwtTokenProvider;

  // 인증번호 생성 및 이메일 발송
  @PostMapping("/code")
  public ResponseEntity<String> sendVerificationEmail(@RequestBody SendEmailRequest sendEmailRequest) {
    try {
      String email = sendEmailRequest.getEmail();
      authService.createVerificationCode(email, emailService);
      return ResponseEntity.ok("인증번호를 보냈습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  // 회원가입
  @PostMapping("/up")
  public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
    try {
      JwtToken jwtToken = authService.createUser(signUpRequest);
      return ResponseEntity.ok(jwtToken);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  // 로그인
  @PostMapping("/in")
  public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {
    try {
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          signInRequest.getEmail(), signInRequest.getPassword());
      Authentication authenticatedUser = authenticationManager.authenticate(authentication);
      SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
      System.out.println(authenticatedUser);
      JwtToken jwtToken = jwtTokenProvider.generateToken(authenticatedUser.getName());
      return ResponseEntity.ok(jwtToken);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  // 이메일/닉네임 중복 체크
  @GetMapping("/validateUnique")
  public ResponseEntity<?> validateUniqueEmailAndNickname(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "nickname", required = false) String nickname) {
    if (email != null && !email.isEmpty()) {
      User emailExists = userService.isEmailDuplicate(email);
      System.out.println(emailExists);
      return emailExists != null ? ResponseEntity.ok("이미 사용중인 이메일입니다.")
          : ResponseEntity.status(204).body("사용가능한 이메일입니다.");
    }

    if (nickname != null && !nickname.isEmpty()) {
      User nicknameExists = userService.isNicknameDuplicate(nickname);
      System.out.println(nicknameExists);
      return nicknameExists != null ? ResponseEntity.ok("이미 사용중인 닉네임입니다.")
          : ResponseEntity.status(204).body("사용가능한 닉네임입니다.");
    }
    return ResponseEntity.ok("이미 사용중인 닉네임입니다.");
  }

}
