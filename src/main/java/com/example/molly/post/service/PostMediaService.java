package com.example.molly.post.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.service.CloudinaryService;
import com.example.molly.post.entity.Post;
import com.example.molly.post.entity.PostMedia;
import com.example.molly.post.repository.PostMediaRepository;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PostMediaService {
  private final CloudinaryService cloudinaryService;
  private final PostMediaRepository postMediaRepository;

  public List<PostMedia> createPostMedia(MultipartFile[] files, Post post) throws IOException {
    List<PostMedia> postMedias = new ArrayList<>();
    for (MultipartFile file : files) {
      Map<String, Object> uploadResult = cloudinaryService.upload(file, "post");
      String fileName = (String) uploadResult.get("public_id");
      String filePath = (String) uploadResult.get("url");
      String fileType = (String) uploadResult.get("resource_type");

      PostMedia postMedia = new PostMedia(post, fileName, fileType, filePath);
      postMedias.add(postMedia);
      postMediaRepository.save(postMedia);
      post.getPostMedias().add(postMedia);
    }
    return postMedias;
  }

  public void deletePostMedia(String publicId) {
    try {
      cloudinaryService.delete(publicId);
    } catch (Exception e) {

    }
  }
}
