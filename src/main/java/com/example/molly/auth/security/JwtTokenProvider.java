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

@Component
public class JwtTokenProvider {
  private final Key key;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  public JwtTokenProvider(@Value("${JWT_SECRET}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(Long userId) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    long now = (new Date()).getTime();
    return Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 600000)).signWith(key).compact();
  }

  public String generateRefreshToken(Long userId) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    long now = (new Date()).getTime();
    return Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 7 * 8640000)).signWith(key).compact();
  }

  public Authentication getAuthentication(String accessToken) {
    Claims claims = getClaims(accessToken);
    Long userId = Long.valueOf(claims.getSubject());
    UserDetails userDetails = userDetailsService.loadUserById(userId);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public Long getUserIdFromToken(String accessToken) {
    Claims claims = getClaims(accessToken);
    return Long.valueOf(claims.getSubject());
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

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

  public Long validateAndGetUserId(String token) {
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
      if (validateToken(token)) {
        return getUserIdFromToken(token);
      } else {
        throw new IllegalArgumentException("Invalid or expired token");
      }
    } else {
      throw new IllegalArgumentException("Invalid token");
    }
  }

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
