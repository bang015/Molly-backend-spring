package com.example.molly.chat.dto;

import java.util.List;

import com.example.molly.user.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
  private Long roomId;
  private ChatMessageDTO latestMessage;
  private List<UserDTO> members;
  private int unReadCount;
}
