package com.example.molly.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.bookmark.repository.BookmarkRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
  private final BookmarkRepository bookmarkRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Transactional
  public boolean togglePostBookmark(Long userId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 게시물입니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

    boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, postId);

    if (isBookmarked) {
      bookmarkRepository.deleteByUserIdAndPostId(userId, postId);
      return false;
    } else {
      Bookmark bookmark = Bookmark.builder().post(post).user(user).build();
      bookmarkRepository.save(bookmark);
      return true;
    }
  }
}
