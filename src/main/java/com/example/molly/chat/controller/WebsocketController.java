package com.example.molly.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.molly.auth.security.JwtTokenProvider;
import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomResponse;
import com.example.molly.chat.dto.CreateChatRoomRequest;
import com.example.molly.chat.dto.SendMessageRequest;
import com.example.molly.chat.service.ChatService;
import com.example.molly.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebsocketController {
  private final SimpMessagingTemplate messagingTemplate;
  private final JwtTokenProvider jwtTokenProvider;
  private final ChatService chatService;

  @MessageMapping("/createChatRoom")
  public void createChatRoom(List<CreateChatRoomRequest> request, SimpMessageHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    Long userId = jwtTokenProvider.validateAndGetUserId(token);
    ChatRoomResponse newChatRoom = chatService.getOrCreateChatRoom(userId, request);
    messagingTemplate.convertAndSendToUser(userId.toString(), "/newChatRoom", newChatRoom);
    if (newChatRoom.getLatestMessage() != null) {
      request.add(new CreateChatRoomRequest(userId, ""));
      request.forEach((member) -> {
        System.out.println("memberID : " + member.getId());
        messagingTemplate.convertAndSendToUser(member.getId().toString(), "/newMessage", newChatRoom);
      });
    }
  }

  @MessageMapping("/sendMessage")
  public void sendMessage(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    Long userId = jwtTokenProvider.validateAndGetUserId(token);
    ChatMessageDTO newMessage = chatService.sendMessage(userId, request.getRoomId(), request.getMessage());
    List<UserDTO> members = chatService.getJoinRoomMembers(userId,
        request.getRoomId());
    int unreadCount = chatService.getUnreadCountByChatRoom(request.getRoomId(), userId);
    messagingTemplate.convertAndSend("/chat/" + request.getRoomId(), newMessage);
    members.forEach(member -> {
      int memberUnreadCount = chatService.getUnreadCountByChatRoom(request.getRoomId(), member.getId());
      ChatRoomResponse memberChatRoomInfo = new ChatRoomResponse(request.getRoomId(), newMessage, members,
          memberUnreadCount);
      messagingTemplate.convertAndSendToUser(member.getId().toString(),
          "/newMessage", memberChatRoomInfo);
      messagingTemplate.convertAndSendToUser(member.getId().toString(),
          "/updateCount", unreadCount);
    });
  }

  @MessageMapping("/messageRead")
  public void messageRead(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    Long userId = jwtTokenProvider.validateAndGetUserId(token);
    chatService.readMessage(request.getRoomId(), userId);
  }

  @MessageMapping("leaveChatRoom")
  public void leaveChatRoom(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");
    Long userId = jwtTokenProvider.validateAndGetUserId(token);
    ChatRoomResponse result  = chatService.leaveChatRoom(request.getRoomId(), userId);
    if(result != null) {
      result.getMembers().forEach(member -> {
        messagingTemplate.convertAndSendToUser(member.getId().toString(),
          "/memberLeft", result);
      });
    }
  }
}
