package com.example.molly.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.EagerTransformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Arrays;

@Service
public class CloudinaryService {
  private final Cloudinary cloudinary;

  public CloudinaryService(
      @Value("${CLOUDINARY_NAME}") String cloudName,
      @Value("${CLOUDINARY_KEY}") String apiKey,
      @Value("${CLOUDINARY_SECRET}") String apiSecret) {
    this.cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret));
  }

  // cloudinary에 이미지 저장
  @SuppressWarnings("unchecked")
  public Map<String, Object> upload(MultipartFile file, String purpose) throws IOException {
    Random random = new Random();
    Map<String, Object> params = new HashMap<>();
    // 저장할 폴더를 설정 (프로필 또는 게시물)
    params.put("folder", "/" + purpose);
    // 현재날짜와 랜덤 숫자를 섞어 id 설정
    params.put("public_id", LocalDateTime.now() + String.valueOf(random.nextInt((int) Math.pow(10, 10))));

    // 게시물일 경우 이미지 사이즈를 동일하게 수정한다
    if ("post".equals(purpose)) {
      params = ObjectUtils.asMap(
          "eager", Arrays.asList(
              new EagerTransformation().width(897).height(897).crop("fill")));
    }

    return cloudinary.uploader().upload(file.getBytes(), params);
  }

  // cloudinary에 저장된 이미지 삭제
  public void delete(String publicId) throws IOException {
    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
  }
}
