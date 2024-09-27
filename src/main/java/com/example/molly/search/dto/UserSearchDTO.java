package com.example.molly.search.dto;

import com.example.molly.user.dto.ProfileImageDTO;
import com.example.molly.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO {
  private Long id;
  private String name;
  private String nickname;
  private ProfileImageDTO profileImage;
  private String type = "user";

  public UserSearchDTO(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.nickname = user.getNickname();
    this.profileImage = user.getProfileImage() != null ? new ProfileImageDTO(user.getProfileImage().getPath()) : null;
  }
}
