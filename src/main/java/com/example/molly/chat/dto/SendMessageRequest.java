package com.example.molly.chat.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
  private Long roomId;
  private String message;
}
