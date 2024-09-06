package com.example.molly.user.dto;

import com.example.molly.user.entity.ProfileImage;

import lombok.Data;

@Data
public class ProfileImageDTO {
  private String path;

  public ProfileImageDTO(
      ProfileImage profileImage) {
    this.path = profileImage.getPath();
  }
}
