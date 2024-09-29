package com.example.molly.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomDTO;
import com.example.molly.chat.entity.ChatMessage;
import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.chat.repository.ChatMembersRepository;
import com.example.molly.chat.repository.ChatMessageRepository;
import com.example.molly.chat.repository.ChatRoomRepository;
import com.example.molly.chat.repository.MessageReadStatusRepository;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMembersRepository chatMembersRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final MessageReadStatusRepository messageReadStatusRepository;

  // 채팅방 리스트
  public PaginationResponse<ChatRoomDTO> getChatRooms(Long userId, int page) {
    int limit = 15;
    PageRequest pageable = PageRequest.of(page - 1, limit);
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    Page<ChatRoom> roomPage = chatMembersRepository.findChatRoomsByUser(user, pageable);
    List<ChatRoomDTO> rooms = getChatRoomsDTOList(roomPage);
    return new PaginationResponse<ChatRoomDTO>(rooms, roomPage.getTotalPages());
  }

  // 채팅방 메시지 리스트
  public List<ChatMessageDTO> getChatRoomMessages(Long userId, Long roomId) {
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    return room.getMessages().stream().map(ChatMessageDTO::new).collect(Collectors.toList());
  }

  // 채팅방 참여맴버(본인 제외)
  public List<UserDTO> getJoinRoomMembers(Long userId, Long roomId) {
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    return room.getUsers().stream()
        .filter(user -> !user.getUser().getId().equals(userId))
        .map(user -> new UserDTO(user.getUser()))
        .collect(Collectors.toList());
  }

  // 읽지 않은 메시지 카운트
  public int getUnreadCount(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    return messageReadStatusRepository.countUnreadMessageByUser(user);
  }

  // ChatRoomsDTO 포멧
  List<ChatRoomDTO> getChatRoomsDTOList(Page<ChatRoom> roomPage) {
    return roomPage.stream().map(room -> {
      Long roomId = room.getId();
      int unReadCount = messageReadStatusRepository.countUnreadMessageByRoom(room);
      ChatMessage latestMessage = chatMessageRepository.findTopByRoomOrderByCreatedAtDesc(room).orElse(null);
      List<UserDTO> members = chatMembersRepository.findMembersByRoom(room).stream().map(UserDTO::new)
          .collect(Collectors.toList());
      return new ChatRoomDTO(roomId, unReadCount, new ChatMessageDTO(latestMessage), members);
    }).collect(Collectors.toList());
  }

  // 채팅방 존재 여부, 권한 확인
  ChatRoom verifyChatRoomUser(Long roomId, Long userId) {
    ChatRoom room = chatRoomRepository.findChatRoomWithMessages(roomId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    boolean userExists = room.getUsers().stream().anyMatch(user -> user.getId().equals(userId));
    if (!userExists) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }
    return room;
  }
}
