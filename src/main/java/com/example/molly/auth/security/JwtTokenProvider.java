package com.example.molly.auth.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.molly.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

// 토큰을 생성하거나 검증하는 Provider
@Component
public class JwtTokenProvider {
  private final Key key;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  public JwtTokenProvider(@Value("${JWT_SECRET}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  // 엑세스 토큰 생성
  public String generateAccessToken(Long userId) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    long now = (new Date()).getTime();
    return Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 600000)).signWith(key).compact();
  }

  // 리프레쉬 토큰 생성
  public String generateRefreshToken(Long userId) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    long now = (new Date()).getTime();
    return Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 7 * 8640000)).signWith(key).compact();
  }

  // 토큰으로 Authentication 객체 생성 후 반환
  public Authentication getAuthentication(String accessToken) {
    Claims claims = getClaims(accessToken);
    Long userId = Long.valueOf(claims.getSubject());
    UserDetails userDetails = userDetailsService.loadUserById(userId);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰에서 userId를 추출
  public Long getUserIdFromToken(String accessToken) {
    Claims claims = getClaims(accessToken);
    return Long.valueOf(claims.getSubject());
  }

  // 토큰에서 Claims을 추출
  public Claims getClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw e;
    } catch (JwtException e) {
      throw e;
    }
  }

  // 리프레쉬 토큰은 쿠키로 관리한다
  // 쿠키에서 리프레쉬 토큰 추출
  public String getRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
