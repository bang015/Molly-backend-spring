package com.example.molly.auth.security;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.molly.auth.dto.CustomUserDetails;
import com.example.molly.auth.service.CustomUserDetailsService;
import com.example.molly.common.service.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// http 요청에서 토큰을 검증하기 위한 필터
//OncePerRequestFilter를 상속받아 각 요청에 한 번만 필터링이 적용되도록 구현
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService userDetailsService;
  private final RedisService redisService;

  @Override
  public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws IOException, ServletException {
    // resolveToken 메서드를 호출하여 Authorization 헤더에서 JWT 토큰을 추출
    String token = resolveToken((HttpServletRequest) request);
    if (token != null) {
      try {
        // 토큰 검증
        if (jwtTokenProvider.validateToken(token)) {
          // 토큰에서 userId 추출
          Long userId = jwtTokenProvider.getUserIdFromToken(token);
          // redis에 캐싱되어있는 userDetails가져오기
          CustomUserDetails userDetails = redisService.get("userId:" + userId, CustomUserDetails.class);
          if (userDetails == null) {
            // redis에 캐싱 정보가 없다면 데이터베이스에서 사용자 정보를 불러오고 캐싱한다
            userDetails = (CustomUserDetails) userDetailsService.loadUserById(userId);
            redisService.save("userId:" + userId, userDetails);
          }
          // 사용자 정보와 권한을 사용해 Authentication 객체 생성
          Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "",
              userDetails.getAuthorities());
          // SecurityContextHolder에 인증 정보를 설정
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 에러를 던져 globalExceptionHandler에서 처리하게 되면 잘못된 토큰이거나 만료되어 발생한 에러도
        // 자격증명이 안됐다는 에러로 발생하기때문에 구분을 위해 에러 메시지를 반환하도록 한다.
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

  // 헤더에서 토큰을 추출해 "Bearer"를 제거하고 토큰 값만을 반환
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
