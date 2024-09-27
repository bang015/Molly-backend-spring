package com.example.molly.search.dto;

import com.example.molly.user.dto.ProfileImageDTO;

import lombok.Data;

@Data
public class HistoryRequest {
  private Long id;
  private String name;
  private String nickname;
  private ProfileImageDTO profileImage;
  private int tagCount;
  private String type;
}
