package com.example.molly.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.chat.dto.ChatMessageDTO;
import com.example.molly.chat.dto.ChatRoomDTO;
import com.example.molly.chat.dto.ChatRoomResponse;
import com.example.molly.chat.dto.CreateChatRoomRequest;
import com.example.molly.chat.entity.ChatMembers;
import com.example.molly.chat.entity.ChatMessage;
import com.example.molly.chat.entity.ChatRoom;
import com.example.molly.chat.entity.MessageReadStatus;
import com.example.molly.chat.entity.MessageType;
import com.example.molly.chat.repository.ChatMembersRepository;
import com.example.molly.chat.repository.ChatMessageRepository;
import com.example.molly.chat.repository.ChatRoomRepository;
import com.example.molly.chat.repository.MessageReadStatusRepository;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMembersRepository chatMembersRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final UserRepository userRepository;
  private final MessageReadStatusRepository messageReadStatusRepository;
  private final EntityManager entityManager;

  // 채팅방 생성
  @Transactional
  public ChatRoomResponse getOrCreateChatRoom(Long userId, List<CreateChatRoomRequest> members) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    if (members.size() == 1) {
      Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateRoom(userId, members.get(0).getId());
      if (existingRoom.isPresent()) {
        return new ChatRoomResponse(existingRoom.get().getId(), null, null, 0);
      }
    }
    // 채팅방 생성
    ChatRoom newChatRoom = ChatRoom.builder().isGroupChat(members.size() > 1).build();
    chatRoomRepository.save(newChatRoom);

    // 생성된 채팅방에 맴버 참여
    List<Long> memberIds = members.stream()
        .map(CreateChatRoomRequest::getId)
        .collect(Collectors.toList());
    List<Long> allMembers = new ArrayList<>(memberIds);
    allMembers.add(userId);

    List<UserDTO> chatMembers = new ArrayList<>();
    for (Long memberId : allMembers) {
      ChatMembers newChatMembers = addMemberToRoom(newChatRoom, memberId, members.size() > 1);
      chatMembers.add(new UserDTO(newChatMembers.getUser()));
    }
    // 단체 채팅이라면 시스템 메시지 생성
    if (members.size() > 1) {
      List<String> memberNames = members.stream()
          .map(CreateChatRoomRequest::getName)
          .collect(Collectors.toList());
      String membersString = String.join(", ", memberNames);
      String systemMessageContent = user.getName() + " 님이 " + membersString + " 님을 초대했습니다.";
      ChatMessage sysMessage = ChatMessage.builder().user(null).room(newChatRoom).type(MessageType.SYSTEM)
          .message(systemMessageContent).build();
      chatMessageRepository.save(sysMessage);
      return new ChatRoomResponse(newChatRoom.getId(), new ChatMessageDTO(sysMessage), chatMembers, 0);
    }
    return new ChatRoomResponse(newChatRoom.getId(), null, null, 0);
  }

  // 채팅방 나가기
  @Transactional
  public ChatRoomResponse leaveChatRoom(Long roomId, Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    if (room.isGroupChat()) {
      chatMembersRepository.deleteMembersByUserAndPost(user, room);
      entityManager.flush();
      entityManager.clear();
      String systemMessageContent = user.getName() + " 님이 채팅방을 나갔습니다.";
      ChatMessage sysMessage = ChatMessage.builder().user(null).room(room).type(MessageType.SYSTEM)
          .message(systemMessageContent).build();
      chatMessageRepository.save(sysMessage);
      entityManager.flush();
      List<UserDTO> members = getJoinRoomMembers(userId, roomId);
      if (members.isEmpty()) {
        chatRoomRepository.delete(room);
        entityManager.flush();
        return null;
      }
      return new ChatRoomResponse(roomId, new ChatMessageDTO(sysMessage), members, 0);
    } else {
      room.getUsers().forEach(member -> {
        if (member.getUser().equals(user)) {
          member.changeIsActive(false);
        }
      });
      return null;
    }
  }

  // 채팅방 맴버 추가
  private ChatMembers addMemberToRoom(ChatRoom chatRoom, Long userId, boolean isActive) {
    ChatMembers chatMember = ChatMembers.builder()
        .user(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다.")))
        .room(chatRoom)
        .isActive(isActive)
        .build();
    chatMembersRepository.save(chatMember);
    return chatMember;
  }

  // 채팅방 리스트
  public PaginationResponse<ChatRoomDTO> getChatRooms(Long userId, int page) {
    int limit = 15;
    PageRequest pageable = PageRequest.of(page - 1, limit);
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    Page<ChatRoom> roomPage = chatMembersRepository.findChatRoomsByUser(user, pageable);
    List<ChatRoomDTO> rooms = getChatRoomsDTOList(roomPage, user);
    return new PaginationResponse<ChatRoomDTO>(rooms, roomPage.getTotalPages());
  }

  // 채팅방 메시지 리스트
  public List<ChatMessageDTO> getChatRoomMessages(Long userId, Long roomId) {
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    return room.getMessages().stream().map(ChatMessageDTO::new).collect(Collectors.toList());
  }

  // 채팅방 참여맴버
  public List<UserDTO> getJoinRoomMembers(Long userId, Long roomId) {
    ChatRoom room = chatRoomRepository.findChatRoomWithUsers(roomId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    return room.getUsers().stream()
        .map(user -> new UserDTO(user.getUser()))
        .collect(Collectors.toList());
  }

  // 메시지 생성
  @Transactional
  public ChatMessageDTO sendMessage(Long userId, Long roomId, String message) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    ChatMessage newMessage = ChatMessage.builder().user(user).room(room).message(message).build();
    chatMessageRepository.save(newMessage);
    room.getUsers().forEach(member -> {
      if (!member.isActive()) {
        member.changeIsActive(true);
        chatMembersRepository.save(member);
      }
      if (!member.getUser().getId().equals(userId)) {
        MessageReadStatus newUnreadMessage = MessageReadStatus.builder().message(newMessage).user(member.getUser())
            .isRead(false).build();
        messageReadStatusRepository.save(newUnreadMessage);
      }
    });
    return new ChatMessageDTO(newMessage);
  }

  // 전체 읽지 않은 메시지 카운트
  public int getUnreadCount(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    return messageReadStatusRepository.countUnreadMessageByUser(user);
  }

  // 해당 채팅방의 읽지 않은 메시지 카운트
  public int getUnreadCountByChatRoom(Long roomId, Long userId) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    return messageReadStatusRepository.countUnreadMessageByRoom(room, user);
  }

  // 메시지 읽음 처리
  @Transactional
  public void readMessage(Long roomId, Long userId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    ChatRoom room = verifyChatRoomUser(roomId, userId);
    messageReadStatusRepository.deleteUnreadMessagesByUserAndRoom(user, room);
  }

  // ChatRoomsDTO 포멧
  List<ChatRoomDTO> getChatRoomsDTOList(Page<ChatRoom> roomPage, User user) {
    return roomPage.stream().map(room -> {
      Long roomId = room.getId();
      int unReadCount = messageReadStatusRepository.countUnreadMessageByRoom(room, user);
      ChatMessage latestMessage = chatMessageRepository.findTopByRoomOrderByCreatedAtDesc(room).orElse(null);
      List<UserDTO> members = chatMembersRepository.findMembersByRoom(room).stream()
          .map(UserDTO::new)
          .collect(Collectors.toList());
      return new ChatRoomDTO(roomId, unReadCount, new ChatMessageDTO(latestMessage), members);
    }).collect(Collectors.toList());
  }

  // 채팅방 존재 여부, 권한 확인
  ChatRoom verifyChatRoomUser(Long roomId, Long userId) {
    ChatRoom room = chatRoomRepository.findChatRoomWithUsers(roomId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    boolean userExists = room.getUsers().stream().anyMatch(member -> member.getUser().getId().equals(userId));
    if (!userExists) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }
    return room;
  }
}
