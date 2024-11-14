package com.example.molly.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.molly.bookmark.repository.BookmarkRepository;
import com.example.molly.common.dto.BaseCountDTO;
import com.example.molly.common.dto.PaginationResponse;
import com.example.molly.common.service.RedisService;
import com.example.molly.follow.repository.FollowRepository;
import com.example.molly.like.repository.LikeRepository;
import com.example.molly.post.dto.PostDTO;
import com.example.molly.post.dto.PostResponse;
import com.example.molly.post.dto.TagPaginationResponse;
import com.example.molly.post.dto.UpdatePostRequest;
import com.example.molly.post.entity.Post;
import com.example.molly.post.entity.PostMedia;
import com.example.molly.post.entity.PostTag;
import com.example.molly.post.entity.Tag;
import com.example.molly.post.repository.PostRepository;
import com.example.molly.post.repository.PostTagRepository;
import com.example.molly.post.repository.TagRepository;
import com.example.molly.user.entity.User;
import com.example.molly.user.service.UserService;

import java.util.Set;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
  private final FollowRepository followRepository;
  private final PostRepository postRepository;
  private final LikeRepository likeRepository;
  private final BookmarkRepository bookmarkRepository;
  private final UserService userService;
  private final PostTagRepository postTagRepository;
  private final PostMediaService postMediaService;
  private final TagService tagService;
  private final TagRepository tagRepository;
  private final RedisService redisService;

  // 게시물 생성
  @Transactional
  public PostResponse post(MultipartFile[] files, String content, List<String> hashTags, Long userId) {
    List<PostMedia> postMedias = new ArrayList<>();
    try {
      User user = userService.getUser(userId);
      Post newPost = Post.builder().user(user).content(content).build();
      postRepository.save(newPost);
      for (String tagName : hashTags) {
        // tag가 이미 존재한다면 tag ID를 아니라면 tag를 생성 후 ID를 받아와 postTag에 저장
        Tag tag = tagService.findOrCreateTag(tagName);
        PostTag postTag = PostTag.builder().post(newPost).tag(tag).build();
        postTagRepository.save(postTag);
      }
      // 게시물 이미지 저장
      postMedias = postMediaService.createPostMedia(files, newPost);
      PostDTO postDTO = getPostDTO(newPost, userId);
      return new PostResponse(postDTO, "게시물이 공유 되었습니다");
    } catch (Exception e) {
      // 에러가 발생하면 Transactional를 통해 데이터베이스는 롤백이 되지만
      // cloudinary에 저장한 이미지는 롤백되지 않기 때문에 삭제해줘야한다
      for (PostMedia media : postMedias) {
        postMediaService.deletePostMedia(media.getName());
      }
      throw new RuntimeException("포스트 저장 중 오류가 발생했습니다.", e);
    }
  }

  // 게시물 삭제
  @Transactional
  public void deletePost(Long postId, Long userId) {
    try {
      // 게시물 존재 여부, 권한 확인
      Post post = verifyPostUser(postId, userId);
      // 삭제 전 게시물에서 사용된 태그 찾기
      List<PostTag> postTags = postTagRepository.findByPost(post);
      List<Tag> tags = postTags.stream().map(PostTag::getTag).collect(Collectors.toList());
      List<PostMedia> medias = new ArrayList<>(post.getPostMedias());
      // 게시물 삭제 post entity에서 orphanRemoval = true 설정했기 때문에
      // 게시물의 댓글, postTag, 좋아요, 북마크등등 관련된 모든 것이 함께 삭제된다
      postRepository.delete(post);
      // 삭제된 게시물에서 사용된 태그를 다른 게시물에서 사용되고 있는지 확인 후 미사용 태그 삭제
      tagService.deleteUnusedTags(tags);
      // cloudinary는 한번에 여러개의 이미지를 삭제할 수 없기때문에 반복문을 통해 하나씩 삭제
      for (PostMedia media : medias) {
        postMediaService.deletePostMedia(media.getName());
      }
      redisService.delete("postId:" + postId);
    } catch (Exception e) {
      throw new RuntimeException("포스트 삭제 중 오류가 발생했습니다.", e);
    }
  }

  // 게시물 수정
  @Transactional
  public PostResponse updatePost(UpdatePostRequest request, Long userId) {
    try {
      // 게시물 존재 여부, 권한 확인
      Post post = verifyPostUser(request.getPostId(), userId);
      // 수정 전 게시물에서 사용된 태그 찾기
      List<PostTag> postTags = new ArrayList<>(postTagRepository.findByPost(post));
      List<Tag> tags = postTags.stream().map(PostTag::getTag).collect(Collectors.toList());
      // 게시물 수정
      post.updateContent(request.getContent());
      // postTag를 수정하기 보단 삭제 후 다시 생성하는 것이 효율적이라 판단했음
      postTagRepository.deleteByPost(post);
      for (String tagName : request.getHashtags()) {
        // tag가 이미 존재한다면 tag ID를 아니라면 tag를 생성 후 ID를 받아와 postTag에 저장
        Tag tag = tagService.findOrCreateTag(tagName);
        PostTag postTag = PostTag.builder().post(post).tag(tag).build();
        postTagRepository.save(postTag);
      }
      // 수정 후 미사용 태그 삭제
      tagService.deleteUnusedTags(tags);
      redisService.delete("postId:" + post.getId());
      PostDTO postDTO = getPostDTO(post, userId);
      return new PostResponse(postDTO, "게시물이 수정 되었습니다");
    } catch (Exception e) {
      throw new RuntimeException("포스트 수정 중 오류가 발생했습니다.", e);
    }
  }

  // 메인 게시물 리스트
  public PaginationResponse<PostDTO> getMainPost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    // 본인이 팔로우 한 유저들의 게시물 찾기
    Page<Post> postPage = postRepository.findPostsByUserIds(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(postPage, userId);
    return new PaginationResponse<PostDTO>(postList, postPage.getTotalPages());
  }

  // 추천 게시물 리스트
  public PaginationResponse<PostDTO> getExplorePost(Long userId, int page, int limit) {
    List<Long> followedUserIds = followRepository.findFollowerUserIds(userId);
    followedUserIds.add(userId);
    Pageable pageable = PageRequest.of(page - 1, limit);
    // 본인이 팔로우한 유저들을 제외한 유저의 게시물 추천
    Page<Post> postPage = postRepository.findPostsByUserIdsNotIn(followedUserIds, pageable);
    List<PostDTO> postList = getPostDTOList(postPage, userId);
    return new PaginationResponse<PostDTO>(postList, postPage.getTotalPages());
  }

  // 유저 게시물 리스트
  public PaginationResponse<PostDTO> getUserPost(Long userId, Long targetUserId, int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> postPage = postRepository.findPostsByUserId(targetUserId, pageable);
    List<PostDTO> postList = getPostDTOList(postPage, userId);
    return new PaginationResponse<PostDTO>(postList, postPage.getTotalPages());
  }

  // 북마크 게시물 리스트
  public PaginationResponse<PostDTO> getBookmarkPost(Long userId, Long targetUserId, int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> postPage = bookmarkRepository.findBookmarkedPostsByUserId(targetUserId, pageable);
    List<PostDTO> postList = getPostDTOList(postPage, userId);
    return new PaginationResponse<PostDTO>(postList, postPage.getTotalPages());
  }

  // 태그 게시물 리스트
  public TagPaginationResponse<PostDTO> getTagPost(Long userId, String tagName, int page, int limit) {
    Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));
    Pageable pageable = PageRequest.of(page - 1, limit);
    Page<Post> postPage = postRepository.findPostsByTagName(tag.getName(), pageable);
    List<PostDTO> postList = getPostDTOList(postPage, userId);
    return new TagPaginationResponse<PostDTO>(postList, postPage.getTotalPages(), postTagRepository.countByTag(tag));
  }

  // PostDTO 포맷
  // 게시물은 좋아요, 북마크 여부, 좋아요 갯수를 함께 보내주기 위해 PostDTO를 생성해 반환
  List<PostDTO> getPostDTOList(Page<Post> posts, Long userId) {
    List<Long> postIds = posts.stream()
        .map(Post::getId)
        .collect(Collectors.toList());
    // 하나씩 체크하면 게시물 수 만큼 쿼리가 발생하기 때문에 한번에 체크 후 postId로 매칭
    Set<Long> likedPostIds = likeRepository.findByUserIdAndPostIdIn(userId, postIds);
    
    Set<Long> bookmarkedPostIds = bookmarkRepository.findByUserIdAndPostIdIn(userId, postIds);

    List<BaseCountDTO> counts = likeRepository.countByPostIds(postIds);
    Map<Long, Long> countMap = counts.stream().collect(Collectors.toMap(BaseCountDTO::getId, BaseCountDTO::getCount));
    return posts.stream().map(post -> {
      boolean isLiked = likedPostIds.contains(post.getId());
      long count = countMap.getOrDefault(post.getId(), 0L);
      boolean isBookmarked = bookmarkedPostIds.contains(post.getId());
      return new PostDTO(post, isLiked, count, isBookmarked);
    }).collect(Collectors.toList());
  }

  // PostDTO 포맷
  PostDTO getPostDTO(Post post, Long userId) {
    boolean isLiked = likeRepository.existsByUserIdAndPostId(userId, post.getId());
    long likeCount = likeRepository.countByPostId(post.getId());
    boolean isBookmarked = bookmarkRepository.existsByUserIdAndPostId(userId, post.getId());
    return new PostDTO(post, isLiked, likeCount, isBookmarked);
  }

  // 게시물 존재 여부, 권한 확인
  Post verifyPostUser(Long postId, Long userId) {
    Post post = getPost(postId);
    if (!post.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("권한이 없습니다.");
    }
    return post;
  }

  public Post getPost(Long postId) {
    Post post = redisService.get("postId:" + postId, Post.class);
    if (post == null) {
      post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
      redisService.save("postId:" + postId, post);
    }
    return post;
  }
}
