package com.example.molly.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class ChatController {

  @GetMapping()
  public ResponseEntity<?> getChatRooms(@RequestParam int page) {
    Long userId = SecurityUtil.getCurrentUserId();
    
    return ResponseEntity.ok("");
  }

}
