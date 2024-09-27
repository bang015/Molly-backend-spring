package com.example.molly.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.example.molly.user.dto.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomsDTO {
  private Long roomId;
  private int unReadCount;
  private ChatMessageDTO latestMessage;
  private List<UserDTO> members;
}
