package com.example.molly.chat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomDTO;
import com.example.molly.chat.dto.ChatRoomDetailResponse;
import com.example.molly.chat.service.ChatService;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.common.util.SecurityUtil;
import com.example.molly.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
  private final ChatService chatService;

  // 채팅방 목록 가져오기
  @GetMapping()
  public ResponseEntity<?> getChatRooms(@RequestParam int page) {
    Long userId = SecurityUtil.getCurrentUserId();
    PaginationResponse<ChatRoomDTO> rooms = chatService.getChatRooms(userId, page);
    return ResponseEntity.ok(rooms);
  }

  // 채팅방의 메시지들 가져오기
  @GetMapping("/details/{roomId}")
  public ResponseEntity<?> getChatRoomMessages(@PathVariable Long roomId) {
    Long userId = SecurityUtil.getCurrentUserId();
    List<ChatMessageDTO> messages = chatService.getChatRoomMessages(userId, roomId);
    List<UserDTO> members = chatService.getJoinRoomMembers(userId, roomId);
    return ResponseEntity.ok(new ChatRoomDetailResponse(messages, members));
  }

  // 전체 읽지 않은 메시지 개수 가져오기
  @GetMapping("/unread")
  public ResponseEntity<?> getUnreadCount() {
    Long userId = SecurityUtil.getCurrentUserId();
    int unreadCount = chatService.getUnreadCount(userId);
    return ResponseEntity.ok(unreadCount);
  }

}
