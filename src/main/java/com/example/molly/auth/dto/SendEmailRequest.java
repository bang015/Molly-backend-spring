package com.example.molly.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter@Setter
public class SendEmailRequest {
  private String email;
}
