package com.example.molly.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomResponse;
import com.example.molly.chat.dto.CreateChatRoomRequest;
import com.example.molly.chat.dto.SendMessageRequest;
import com.example.molly.chat.service.ChatService;
import com.example.molly.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("null")
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebsocketController {
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatService chatService;

  @MessageMapping("/createChatRoom")
  public void createChatRoom(List<CreateChatRoomRequest> request, SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

    // 채팅방 생성 또는 기존 채팅방 가져오기
    ChatRoomResponse newChatRoom = chatService.getOrCreateChatRoom(userId, request);

    // 채팅방 아이디를 채팅방을 생성한 유저에게 전송
    messagingTemplate.convertAndSendToUser(userId.toString(), "/newChatRoom", newChatRoom);

    // 개인 채팅방이라면 채팅방 생성 후 메시지를 보내야만 채팅방 목록에 추가되지만,
    // 단체 채팅방이라면 시스템 메시지와 함께 채팅방 목록에 추가되어야하기 때문에 채팅방 정보를 참여 맴버들에게 전송
    if (newChatRoom.getLatestMessage() != null) {
      request.add(new CreateChatRoomRequest(userId, ""));
      request.forEach((member) -> {
        messagingTemplate.convertAndSendToUser(member.getId().toString(), "/newMessage", newChatRoom);
      });
    }

  }

  @MessageMapping("/sendMessage")
  public void sendMessage(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    // 새로운 메시지 생성
    ChatMessageDTO newMessage = chatService.sendMessage(userId,
        request.getRoomId(), request.getMessage());
    // 채팅방에 참여중인 맴버
    List<UserDTO> members = chatService.getJoinRoomMembers(userId,
        request.getRoomId());
    // 해당 채팅방 읽지 않은 메시지 카운트
    int unreadCount = chatService.getUnreadCountByChatRoom(request.getRoomId(),
        userId);
    // 채팅방을 구독중인 유저에게 메시지 전송
    messagingTemplate.convertAndSend("/chat/" + request.getRoomId(), newMessage);
    // 채팅방에 참여중이지만 구독하지 않은(접속중이 아닌) 유저에게 메시지 전송
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

  // 메시지 읽음 처리
  @MessageMapping("/messageRead")
  public void messageRead(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    chatService.readMessage(request.getRoomId(), userId);
  }

  // 채팅방 나가기
  @MessageMapping("leaveChatRoom")
  public void leaveChatRoom(SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
    Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
    // 채팅방 나간 후 참여 맴버
    ChatRoomResponse result = chatService.leaveChatRoom(request.getRoomId(), userId);
    // 해당 유저가 채팅방을 나갔다고 메시지 전송
    if (result != null) {
      result.getMembers().forEach(member -> {
        messagingTemplate.convertAndSendToUser(member.getId().toString(),
            "/memberLeft", result);
      });
    }
  }
}
