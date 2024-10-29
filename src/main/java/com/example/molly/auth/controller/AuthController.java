package com.example.molly.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    authService.resetPassword(request);
    return ResponseEntity.ok("비밀번호 재설정을 성공했습니다.");
  }

  // 회원가입
  @PostMapping("/up")
  public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest, HttpServletResponse response) {
    JwtToken jwtToken = authService.createUser(signUpRequest);
    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken()).httpOnly(true)
        .secure(true).path("/").maxAge(7 * 24 * 60 * 60).build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    return ResponseEntity.ok(jwtToken.getAccessToken());
  }

  // 로그인
  @PostMapping("/in")
  public ResponseEntity<?> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse response) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        signInRequest.getEmail(), signInRequest.getPassword());
    Authentication authenticatedUser = authenticationManager.authenticate(authentication);
    Long userId = Long.valueOf(authenticatedUser.getName());
    String accessToken = jwtTokenProvider.generateAccessToken(userId);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userId);
    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
        .secure(true).path("/").maxAge(7 * 24 * 60 * 60).build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    return ResponseEntity.ok(accessToken);
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
  public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = jwtTokenProvider.getRefreshTokenFromCookie(request);
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new JwtException("잘못된 토큰입니다.");
    }
    Claims claims = jwtTokenProvider.getClaims(refreshToken);
    Long userId = Long.valueOf(claims.getSubject());
    String newTokens = jwtTokenProvider.generateAccessToken(userId);
    return ResponseEntity.ok(newTokens);
  }

}
