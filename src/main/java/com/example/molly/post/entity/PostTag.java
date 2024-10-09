package com.example.molly.post.entity;

import com.example.molly.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`PostTag`")
public class PostTag extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "postId")
  private Post post;

  @ManyToOne
  @JoinColumn(name = "tagId")
  private Tag tag;
}
