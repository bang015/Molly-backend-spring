package com.example.molly.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.molly.bookmark.entity.Bookmark;
import com.example.molly.bookmark.repository.BookmarkRepository;
import com.example.molly.post.entity.Post;
import com.example.molly.post.service.PostService;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
  private final BookmarkRepository bookmarkRepository;
  private final UserService userService;
  private final PostService postService;

  // 북마크 추가/해제
  @Transactional
  public boolean togglePostBookmark(Long userId, Long postId) {
    Post post = postService.getPost(postId);
    User user = userService.getUser(userId);
    // 북마크 여부 확인 후 추가/ 해제
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
