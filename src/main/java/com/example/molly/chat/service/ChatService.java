package com.example.molly.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomsDTO;
import com.example.molly.chat.entity.ChatMessage;
import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.chat.repository.ChatMembersRepository;
import com.example.molly.chat.repository.ChatMessageRepository;
import com.example.molly.chat.repository.ChatRoomRepository;
import com.example.molly.chat.repository.MessageReadStatusRepository;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatMembersRepository chatMembersRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final MessageReadStatusRepository messageReadStatusRepository;

  public List<ChatRoomsDTO> getChatRooms(Long userId, int page) {
    int limit = 15;
    PageRequest pageable = PageRequest.of(page - 1, limit);
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

    Page<ChatRoom> roomPage = chatMembersRepository.findChatRoomsByUser(user, pageable);
    return getChatRoomsDTOList(roomPage);
  }

  List<ChatRoomsDTO> getChatRoomsDTOList(Page<ChatRoom> roomPage) {
    return roomPage.stream().map(room -> {
      Long roomId = room.getId();
      int unReadCount = messageReadStatusRepository.countUnreadMessageByRoom(room);
      ChatMessage latestMessage = chatMessageRepository.findTopByRoomOrderByCreatedAtDesc(room).orElse(null);
      List<UserDTO> members = chatMembersRepository.findMembersByRoom(room).stream().map(UserDTO::new)
          .collect(Collectors.toList());
      return new ChatRoomsDTO(roomId, unReadCount, new ChatMessageDTO(latestMessage), members);
    }).collect(Collectors.toList());
  }
}
