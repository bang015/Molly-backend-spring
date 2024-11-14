package com.example.molly.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.molly.auth.dto.JwtToken;
import com.example.molly.auth.dto.PasswordResetRequest;
import com.example.molly.auth.dto.SignUpRequest;
import com.example.molly.auth.entity.Verification;
import com.example.molly.auth.repository.AuthRepository;
import com.example.molly.auth.security.JwtTokenProvider;
import com.example.molly.common.service.EmailService;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;
  @Value("${REQ_ADDRESS}")
  private String reqAddress;

  // 인증번호 생성 및 이메일 발송
  @Transactional
  public void sendVerificationCode(String email) {
    try {
      // 인증번호 생성
      String verificationCode = createVerificationCode(email);
      String subject = "Your Verification Code";
      String text = "인증번호: " + verificationCode;
      // 이메일 전송
      emailService.sendVerificationEmail(email, subject, text);
    } catch (Exception e) {
      throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.", e);
    }
  }

  // 인증번호 생성 및 이메일 링크 보내기
  @Transactional
  public void sendPasswordResetLink(String email) {
    try {
      // 인증번호 생성
      String verificationCode = createVerificationCode(email);
      String resetLink = reqAddress + "/auth/password/reset?code=" + verificationCode + "&email=" + email;
      String subject = "비밀번호 재설정 요청";
      String text = "<p>비밀번호를 재설정하려면 <a href=\"" + resetLink + "\">여기</a>를 클릭하세요.</p>";
      // 이메일 전송
      emailService.sendVerificationEmail(email, subject, text);
    } catch (Exception e) {
      throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.", e);
    }
  }

  // 인증번호 생성
  @Transactional
  private String createVerificationCode(String email) {
    // 기존 인증번호 삭제
    authRepository.deleteByEmail(email);
    String verificationCode = UUID.randomUUID().toString().substring(0, 6);
    Verification verification = Verification.builder().email(email).code(verificationCode).build();
    // 인증번호 저장
    authRepository.save(verification);
    return verificationCode;
  }

  // 인증번호 검증
  public void verificationCode(String email, String code) {
    Optional<Verification> verificationOptional = authRepository.findByEmail(email);
    Verification verification = verificationOptional
        .orElseThrow(() -> new IllegalArgumentException("등록된 인증번호가 없습니다. 이메일을 확인하고 인증번호를 요청해 주세요."));
    LocalDateTime expiresAt = verification.getExpiresAt();
    LocalDateTime currentTime = LocalDateTime.now(ZoneId.systemDefault());
    if (currentTime.isAfter(expiresAt)) {
      throw new IllegalArgumentException("인증번호의 유효기간이 만료되었습니다. 새로운 인증번호를 요청해 주세요.");
    }
    if (!verification.getCode().equals(code)) {
      throw new IllegalArgumentException("입력한 인증번호가 올바르지 않습니다. 다시 시도해 주세요.");
    }
  }

  // 회원가입
  @Transactional
  public JwtToken createUser(SignUpRequest signUpRequest) {
    // 인증번호 검증
    verificationCode(signUpRequest.getEmail(), signUpRequest.getCode());
    // 유저 생성
    User user = User.builder().email(signUpRequest.getEmail()).name(signUpRequest.getName())
        .nickname(signUpRequest.getNickname()).password(signUpRequest.getPassword()).build();
    try {
      // 유저 정보 저장
      userRepository.save(user);
      // 인증번호 삭제
      authRepository.deleteByEmail(signUpRequest.getEmail());
      // 토큰 생성
      String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
      String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
      return JwtToken.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    } catch (Exception e) {
      throw new RuntimeException("회원가입 중 오류가 발생했습니다.");
    }
  }

  // 비밀번호 초기화
  @Transactional
  public void resetPassword(PasswordResetRequest request) {
    verificationCode(request.getEmail(), request.getCode());
    try {
      User user = userRepository.findByEmail(request.getEmail())
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
      user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
      authRepository.deleteByEmail(request.getEmail());
    } catch (Exception e) {
      throw new RuntimeException("비밀번호 재설정 중 오류가 발생했습니다.");
    }
  }
}
