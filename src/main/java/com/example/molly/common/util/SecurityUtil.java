package com.example.molly.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
  public static Long getCurrentUserId() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getName() == null) {
      throw new RuntimeException("No authentication information.");
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Long userId = Long.valueOf(userDetails.getUsername());
    return userId;
  }
}
