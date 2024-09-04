package com.example.molly.post.entity;

import java.util.List;
import com.example.molly.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag extends BaseEntity {
  @ManyToMany(mappedBy = "tags")
  private List<Post> posts;
  @Column(nullable = false)
  private String name;
}
