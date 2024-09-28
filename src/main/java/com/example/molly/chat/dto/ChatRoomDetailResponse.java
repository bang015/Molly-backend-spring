package com.example.molly.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.example.molly.user.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetailResponse {
  private List<ChatMessageDTO> message;
  private List<UserDTO> members;
}
