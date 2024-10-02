package com.example.molly.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.molly.auth.dto.JwtRequest;
import com.example.molly.auth.dto.JwtToken;
import com.example.molly.auth.dto.PasswordResetRequest;
import com.example.molly.auth.dto.SendEmailRequest;
import com.example.molly.auth.dto.SignInRequest;
import com.example.molly.auth.dto.SignUpRequest;
import com.example.molly.auth.security.JwtTokenProvider;
import com.example.molly.auth.service.AuthService;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
  private final JwtTokenProvider jwtTokenProvider;

  // 인증번호 생성 및 이메일 발송
  @PostMapping("/code")
  public ResponseEntity<String> sendVerificationEmail(@RequestBody SendEmailRequest sendEmailRequest) {
    String email = sendEmailRequest.getEmail();
    authService.sendVerificationCode(email);
    return ResponseEntity.ok("인증번호를 보냈습니다.");
  }

  // 비밀번호 재설정 링크 발송
  @PostMapping("/link")
  public ResponseEntity<String> sendPasswordResetLink(@RequestBody SendEmailRequest sendEmailRequest) {
    String email = sendEmailRequest.getEmail();
    authService.sendPasswordResetLink(email);
    return ResponseEntity.ok("비밀번호 재설정 링크를 보냈습니다.");
  }

  // 비밀번호 재설정
  @PostMapping("/reset/password")
  public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
    System.out.println(request);
    authService.resetPassword(request);
    return ResponseEntity.ok("비밀번호 재설정을 성공했습니다.");
  }

  // 회원가입
  @PostMapping("/up")
  public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
    JwtToken jwtToken = authService.createUser(signUpRequest);
    return ResponseEntity.ok(jwtToken);
  }

  // 로그인
  @PostMapping("/in")
  public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        signInRequest.getEmail(), signInRequest.getPassword());
    Authentication authenticatedUser = authenticationManager.authenticate(authentication);
    Long userId = Long.valueOf(authenticatedUser.getName());
    JwtToken jwtToken = jwtTokenProvider.generateToken(userId);
    return ResponseEntity.ok(jwtToken);
  }

  // 이메일/닉네임 중복 체크
  @GetMapping("/validateUnique")
  public ResponseEntity<?> validateUniqueEmailAndNickname(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String nickname) {
    if (email != null && !email.isEmpty()) {
      User emailExists = userService.findUserByEmail(email);
      return emailExists != null ? ResponseEntity.ok("이미 사용중인 이메일입니다.")
          : ResponseEntity.status(204).body("사용가능한 이메일입니다.");
    }
    if (nickname != null && !nickname.isEmpty()) {
      User nicknameExists = userService.findUserByNickname(nickname);
      return nicknameExists != null ? ResponseEntity.ok("이미 사용중인 닉네임입니다.")
          : ResponseEntity.status(204).body("사용가능한 닉네임입니다.");
    }
    return ResponseEntity.ok("이미 사용중인 닉네임입니다.");
  }

  // 리프레쉬 토큰
  @PostMapping("/token")
  public ResponseEntity<?> refreshToken(@RequestBody JwtRequest jwtRequest) {
    System.out.println("jwtRequest" + jwtRequest);
    if (!jwtTokenProvider.validateToken(jwtRequest.getRefreshToken())) {
      throw new JwtException("잘못된 토큰입니다.");
    }
    Claims claims = jwtTokenProvider.getClaims(jwtRequest.getRefreshToken());
    Long userId = Long.valueOf(claims.getSubject());
    JwtToken newTokens = jwtTokenProvider.generateToken(userId);
    return ResponseEntity.ok(newTokens);
  }

}
