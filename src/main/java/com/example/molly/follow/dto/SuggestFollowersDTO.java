package com.example.molly.follow.dto;

import com.example.molly.user.dto.UserResponseDTO;
import com.example.molly.user.entity.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SuggestFollowersDTO extends UserResponseDTO {
  private String message;
  private boolean isFollowed = false;

  public SuggestFollowersDTO(User user, String message, boolean isFollowed) {
    super(user);
    this.message = message;
    this.isFollowed = isFollowed;
  }
}
