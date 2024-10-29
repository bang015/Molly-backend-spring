package com.example.molly.auth.security;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

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
  public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws IOException, ServletException {
    String token = resolveToken((HttpServletRequest) request);

    if (token != null) {
      try {
        if (jwtTokenProvider.validateToken(token)) {
          Authentication authentication = jwtTokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (ExpiredJwtException ex) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("기간이 만료된 토큰입니다.");
        return;
      } catch (JwtException ex) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write("잘못된 토큰입니다.");
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
