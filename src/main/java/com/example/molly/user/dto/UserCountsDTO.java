package com.example.molly.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCountsDTO {
  private Long postCount;
  private Long followerCount;
  private Long followingCount;
}
