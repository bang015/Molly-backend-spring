package com.example.molly.post.entity;

import java.util.ArrayList;
import java.util.List;
import com.example.molly.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseEntity {
  @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PostTag> postTags = new ArrayList<>();

  @Column(nullable = false)
  private String name;
}
