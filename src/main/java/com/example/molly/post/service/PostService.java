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
import com.example.molly.post.dto.PostListResponse;
import com.example.molly.post.dto.PostResponse;
import com.example.molly.post.dto.UpdatePostRequest;
import com.example.molly.post.entity.Post;
import com.example.molly.post.entity.PostMedia;
import com.example.molly.post.entity.PostTag;
import com.example.molly.post.entity.Tag;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.post.repository.PostTagRepository;
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
  private final PostTagRepository postTagRepository;
  private final PostMediaService postMediaService;
  private final TagService tagService;

  @Transactional
  public PostResponse post(MultipartFile[] files, String content, List<String> hashTags, Long userId) {
    List<PostMedia> postMedias = new ArrayList<>();
    try {
      User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
      Post newPost = Post.builder().user(user).content(content).build();
      postRepository.save(newPost);
      for (String tagName : hashTags) {
        Tag tag = tagService.findOrCreateTag(tagName);
        PostTag postTag = PostTag.builder().post(newPost).tag(tag).build();
        postTagRepository.save(postTag);
      }
      postMedias = postMediaService.createPostMedia(files, newPost);
      Post post = postRepository.findById(newPost.getId()).get();
      PostDTO postDTO = getPostDTO(post, userId);
      return new PostResponse(postDTO, "게시물이 공유 되었습니다");

    } catch (Exception e) {
      System.out.println(e);
      for (PostMedia media : postMedias) {
        postMediaService.deletePostMedia(media.getName());
      }
      throw new RuntimeException("포스트 저장 중 오류가 발생했습니다.", e);
    }
  }

  @Transactional
  public void deletePost(Long postId, Long userId) {
    try {
      Post post = verifyPostUser(postId, userId);
      List<PostTag> postTags = postTagRepository.findByPost(post);
      List<Tag> tags = postTags.stream().map(PostTag::getTag).collect(Collectors.toList());
      List<PostMedia> medias = new ArrayList<>(post.getPostMedias());
      postRepository.delete(post);
      tagService.deleteUnusedTags(tags);
      for (PostMedia media : medias) {
        postMediaService.deletePostMedia(media.getName());
      }
    } catch (Exception e) {
      System.out.println(e);
      throw new RuntimeException("포스트 삭제 중 오류가 발생했습니다.", e);
    }
  }

  @Transactional
  public PostResponse updatePost(UpdatePostRequest request, Long userId) {
    try {
      Post post = verifyPostUser(request.getPostId(), userId);
      List<PostTag> postTags = new ArrayList<>(postTagRepository.findByPost(post));
      List<Tag> tags = postTags.stream().map(PostTag::getTag).collect(Collectors.toList());
      post.updateContent(request.getContent());
      postTagRepository.deleteByPost(post);
      for (String tagName : request.getHashtags()) {
        Tag tag = tagService.findOrCreateTag(tagName);
        PostTag postTag = PostTag.builder().post(post).tag(tag).build();
        postTagRepository.save(postTag);
      }
      tagService.deleteUnusedTags(tags);

      PostDTO postDTO = getPostDTO(post, userId);
      return new PostResponse(postDTO, "게시물이 수정 되었습니다");
    } catch (Exception e) {
      System.out.println(e);
      throw new RuntimeException("포스트 수정 중 오류가 발생했습니다.", e);
    }
  }

  public PostListResponse getMainPost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIds(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(posts, userId);
    return new PostListResponse(postList, posts.getTotalPages());
  }

  public PostListResponse getExplorePost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> posts = postRepository.findPostsByUserIdsNotIn(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(posts, userId);
    return new PostListResponse(postList, posts.getTotalPages());
  }

  List<PostDTO> getPostDTOList(Page<Post> posts, Long userId) {
    return posts.stream().map(post -> {
      boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
      long likeCount = likeRepository.countByPostId(post.getId());
      boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
      return new PostDTO(post, isLiked, likeCount, isBookmarked);
    }).collect(Collectors.toList());
  }

  PostDTO getPostDTO(Post post, Long userId) {
    boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
    long likeCount = likeRepository.countByPostId(post.getId());
    boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
    return new PostDTO(post, isLiked, likeCount, isBookmarked);
  }

  Post verifyPostUser(Long postId, Long userId) {
    Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    if (!post.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }
    return post;
  }
}
