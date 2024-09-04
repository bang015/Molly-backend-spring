package com.example.molly.auth.entity;

import java.time.LocalDateTime;

import com.example.molly.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
@Entity
public class Verification extends BaseEntity {
  @Column(nullable = false)
  @Email(message = "이메일 형식이 아닙니다.")
  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;

  @Column(nullable = false)
  @NotBlank(message = "인증번호를 입력해주세요.")
  private String code;
  
  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @PrePersist
    public void prePersist() {
        this.expiresAt = LocalDateTime.now().plusMinutes(10);
    }
}
