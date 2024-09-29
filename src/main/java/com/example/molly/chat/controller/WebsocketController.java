package com.example.molly.chat.controller;

import java.util.Arrays;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.molly.auth.security.JwtTokenProvider;
import com.example.molly.chat.dto.CreateChatRoomRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
  private final SimpMessagingTemplate messagingTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  @MessageMapping("/createChatRoom")
  public void createChatRoom(CreateChatRoomRequest[] request) {
    System.out.println(Arrays.toString(request));
  }

}
