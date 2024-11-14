package com.example.molly.common.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  // redis에 객체를 JSON 문자열로 변환하여 저장
  public void save(String key, Object object) {
    try {
      String jsonString = objectMapper.writeValueAsString(object);
      redisTemplate.opsForValue().set(key, jsonString);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  // redis에 저장된 데이터를 객체로 변환 후 반환
  public <T> T get(String key, Class<T> clazz) {
    String jsonString = redisTemplate.opsForValue().get(key);
    try {
      return jsonString != null ? objectMapper.readValue(jsonString, clazz) : null;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void delete(String key){
    redisTemplate.delete(key);
  }
}
