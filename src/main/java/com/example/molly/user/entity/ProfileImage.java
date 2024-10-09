package com.example.molly.user.entity;

import com.example.molly.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`ProfileImage`")
public class ProfileImage extends BaseEntity {
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String type;
  @Column(nullable = false)
  private String path;
}
