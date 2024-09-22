package com.example.molly.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.bookmark.repository.BookmarkRepository;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.like.repository.LikeRepository;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponseDTO;
import com.example.molly.post.entity.Post;
import com.example.molly.post.entity.PostMedia;
import com.example.molly.post.entity.Tag;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  private final FollowRepository followRepository;
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final PostMediaService postMediaService;
  private final TagService tagService;

  @Transactional
  public PostDTO post(MultipartFile[] files, String content, List<String> hashTags, Long UserId) {
    List<PostMedia> postMedias = new ArrayList<>();
    try {
      User user = userRepository.findById(UserId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
      List<Tag> tags = new ArrayList<>();
      for (String tagName : hashTags) {
        Tag tag = tagService.findOrCreateTag(tagName);
        tags.add(tag);
      }
      Post newPost = Post.builder().user(user).content(content).tags(tags).build();
      postRepository.save(newPost);
      postMedias = postMediaService.createPostMedia(files, newPost);
      Post post = postRepository.findById(newPost.getId()).get();
      PostDTO postDTO = new PostDTO(post, false, 0, false);
      System.out.println(postDTO);

      return postDTO;

    } catch (Exception e) {
      System.out.println(e);
      for (PostMedia media : postMedias) {
        postMediaService.deletePostMedia(media.getName());
      }
      throw new RuntimeException("포스트 저장 중 오류가 발생했습니다.", e);
    }
  }

  public PostResponseDTO getMainPost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIds(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(posts, userId);
    return new PostResponseDTO(postList, posts.getTotalPages());
  }

  public PostResponseDTO getExplorePost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIdsNotIn(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(posts, userId);
    return new PostResponseDTO(postList, posts.getTotalPages());
  }

  List<PostDTO> getPostDTOList(Page<Post> posts, Long userId) {
    return posts.stream().map(post -> {
      boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
      long likeCount = likeRepository.countByPostId(post.getId());
      boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
      return new PostDTO(post, isLiked, likeCount, isBookmarked);
    }).collect(Collectors.toList());
  }
}
