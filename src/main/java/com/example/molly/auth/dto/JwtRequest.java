package com.example.molly.auth.dto;

import lombok.Data;

@Data
public class JwtRequest {
  private String refreshToken;
}
