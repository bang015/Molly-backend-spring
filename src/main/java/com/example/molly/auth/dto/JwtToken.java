package com.example.molly.auth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtToken {
  private String accessToken;
  private String refreshToken;
}
