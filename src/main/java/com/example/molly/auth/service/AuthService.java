package com.example.molly.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.molly.auth.dto.JwtToken;
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

  // 인증번호 생성
  @Transactional
  public void createVerificationCode(String email, EmailService emailService) {
    String verificationCode = null;
    try {
      authRepository.deleteByEmail(email);
      verificationCode = UUID.randomUUID().toString().substring(0, 6);
      Verification verification = new Verification();
      verification.setEmail(email);
      verification.setCode(verificationCode);
      authRepository.save(verification);
      String subject = "Your Verification Code";
      String text = "인증번호: " + verificationCode;
      emailService.sendVerificationEmail(email, subject, text);
    } catch (Exception e) {
      if (verificationCode != null) {
        authRepository.deleteByEmail(email);
      }
      throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.", e);
    }
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
    verificationCode(signUpRequest.getEmail(), signUpRequest.getCode());
    User user = new User(signUpRequest);
    try {
      userRepository.save(user);
      authRepository.deleteByEmail(signUpRequest.getEmail());
      return jwtTokenProvider.generateToken(user.getId());
    } catch (Exception e) {
      System.out.println(e);
      throw new RuntimeException("회원가입에 실패했습니다.");
    }
  }
}
