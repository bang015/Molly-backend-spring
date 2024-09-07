package com.example.molly.auth.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilterInternal(@NonNull HttpServletRequest requset, @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws IOException, ServletException {
    String token = resolveToken((HttpServletRequest) requset);

    if (token != null && jwtTokenProvider.validateToken(token)) {
      Authentication authentication = jwtTokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(requset, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
