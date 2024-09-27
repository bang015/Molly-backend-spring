package com.example.molly.user.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.molly.common.service.CloudinaryService;
import com.example.molly.user.dto.UserDTO;
import com.example.molly.user.entity.ProfileImage;
import com.example.molly.user.entity.User;
import com.example.molly.user.repository.ProfileImageRepository;
import com.example.molly.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
  private final CloudinaryService cloudinaryService;
  private final UserRepository userRepository;
  private final ProfileImageRepository profileImageRepository;

  // 프로필 이미지 수정
  @Transactional
  public UserDTO updateProfileImage(Long userId, MultipartFile profileImage) {
    try {
      User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾지 못했습니다."));
      if (user.getProfileImage() != null) {
        cloudinaryService.delete(user.getProfileImage().getName());
        profileImageRepository.delete(user.getProfileImage());
      }
      Map<String, Object> uploadResult = cloudinaryService.upload(profileImage, "profile");
      String fileName = (String) uploadResult.get("public_id");
      String filePath = (String) uploadResult.get("url");
      String fileType = (String) uploadResult.get("resource_type");
      ProfileImage newProfileImage = new ProfileImage(fileName, fileType, filePath);
      profileImageRepository.save(newProfileImage);
      user.updateProfileImage(newProfileImage);
      return new UserDTO(user);
    } catch (IOException e) {
      throw new RuntimeException("프로필 수정 중 오류가 발생했습니다.", e);
    }
  }
}
