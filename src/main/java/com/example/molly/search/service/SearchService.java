package com.example.molly.search.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.molly.post.repository.PostTagRepository;
import com.example.molly.post.repository.TagRepository;
import com.example.molly.search.dto.HistoryRequest;
import com.example.molly.search.dto.TagSearchDTO;
import com.example.molly.search.dto.UserSearchDTO;
import com.example.molly.user.repository.UserRepository;
import java.util.stream.Collectors;
import java.util.List;
import lombok.RequiredArgsConstructor;
import java.util.stream.Stream;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SearchService {
  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final static String SEARCH_HISTORY_KEY = "searchHistory:";
  private final ObjectMapper objectMapper;

  public List<Object> search(String keyword, String type) {
    if (type.equalsIgnoreCase("user")) {
      return userRepository.searchUsersByKeyword(keyword).stream().map(UserSearchDTO::new).collect(Collectors.toList());
    } else if (type.equalsIgnoreCase("tag")) {
      return tagRepository.searchTagsByKeyword(keyword).stream()
          .map(tag -> new TagSearchDTO(tag, postTagRepository.countByTag(tag))).collect(Collectors.toList());
    } else {
      List<UserSearchDTO> users = userRepository.searchUsersByKeyword(keyword)
          .stream()
          .map(UserSearchDTO::new)
          .collect(Collectors.toList());

      List<TagSearchDTO> tags = tagRepository.searchTagsByKeyword(keyword)
          .stream()
          .map(tag -> new TagSearchDTO(tag, postTagRepository.countByTag(tag)))
          .collect(Collectors.toList());

      return Stream.concat(users.stream(), tags.stream()).collect(Collectors.toList());
    }
  }

  public void updateSearchHistory(Long userId, HistoryRequest historyRequest) throws JsonProcessingException {
    String redisKey = SEARCH_HISTORY_KEY + userId;
    List<String> historyList = redisTemplate.opsForList().range(redisKey, 0, -1);
    if (historyList != null) {
      for (String historyJson : historyList) {
        HistoryRequest storedHistory = objectMapper.readValue(historyJson, HistoryRequest.class);
        if (storedHistory.getName().equals(historyRequest.getName())) {
          redisTemplate.opsForList().remove(redisKey, 1, historyJson);
          break;
        }
      }
    }
    String historyRequestJson = objectMapper.writeValueAsString(historyRequest);
    redisTemplate.opsForList().leftPush(redisKey, historyRequestJson);
    redisTemplate.opsForList().trim(redisKey, 0, 9);
  }

  public List<HistoryRequest> getSearchHistory(Long userId) throws JsonMappingException, JsonProcessingException {
    String redisKey = SEARCH_HISTORY_KEY + userId;
    List<String> jsonResults = redisTemplate.opsForList().range(redisKey, 0, -1);
    List<HistoryRequest> searchHistory = new ArrayList<>();
    if (jsonResults != null) {
      for (String jsonResult : jsonResults) {
        HistoryRequest historyRequest = objectMapper.readValue(jsonResult, HistoryRequest.class);
        searchHistory.add(historyRequest);
      }
    }
    return searchHistory;
  }

  public void deleteSearchHistory(Long userId, HistoryRequest historyRequest)
      throws JsonMappingException, JsonProcessingException {
    String redisKey = SEARCH_HISTORY_KEY + userId;
    if (historyRequest == null) {
      redisTemplate.delete(redisKey);
    } else {
      List<String> historyList = redisTemplate.opsForList().range(redisKey, 0, -1);
      if (historyList != null) {
        for (String historyJson : historyList) {
          HistoryRequest storedHistory = objectMapper.readValue(historyJson, HistoryRequest.class);
          if (storedHistory.getName().equals(historyRequest.getName())) {
            redisTemplate.opsForList().remove(redisKey, 0, historyJson);
          }
        }
      }
    }
  }
}
