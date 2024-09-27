package com.example.molly.search.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.molly.common.util.SecurityUtil;
import com.example.molly.search.dto.HistoryRequest;
import com.example.molly.search.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
  private final SearchService searchService;

  @PostMapping("/history")
  public void updateSearchHistory(@RequestBody HistoryRequest historyRequest) throws JsonProcessingException {
    Long userId = SecurityUtil.getCurrentUserId();
    searchService.updateSearchHistory(userId, historyRequest);
  }

  @GetMapping("/{type}")
  public ResponseEntity<?> getSearchResult(@PathVariable String type, @RequestParam String query) {
    List<Object> result = searchService.search(query, type);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/history")
  public ResponseEntity<?> getSearchHistory() throws JsonMappingException, JsonProcessingException {
    Long userId = SecurityUtil.getCurrentUserId();
    List<HistoryRequest> result = searchService.getSearchHistory(userId);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/history")
  public void deleteSearchHistory(@RequestBody(required = false)  HistoryRequest historyRequest)
      throws JsonMappingException, JsonProcessingException {
    Long userId = SecurityUtil.getCurrentUserId();
    System.out.println(historyRequest);
    searchService.deleteSearchHistory(userId, historyRequest);
  }
}
