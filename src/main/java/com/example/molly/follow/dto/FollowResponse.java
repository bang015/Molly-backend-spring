package com.example.molly.follow.dto;

import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FollowResponse extends UserDTO {
  private String message;
  @JsonProperty("isFollowed")
  private boolean isFollowed = false;

  public FollowResponse(User user, String message, boolean isFollowed) {
    super(user);
    this.message = message;
    this.isFollowed = isFollowed;
  }
}
