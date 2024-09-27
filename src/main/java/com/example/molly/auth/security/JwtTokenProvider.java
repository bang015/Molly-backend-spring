package com.example.molly.auth.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.molly.auth.dto.JwtToken;
import com.example.molly.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
  private final Key key;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public JwtToken generateToken(Long userId) {
    Claims claims = Jwts.claims().setSubject(userId.toString());
    long now = (new Date()).getTime();
    String accessToken = Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 8640000)).signWith(key).compact();
    String refreshToken = Jwts.builder().setClaims(claims)
        .setExpiration(new Date(now + 7 * 8640000)).signWith(key).compact();
    return JwtToken.builder()
        .grantType("Bearer")
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
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
    } catch (Exception e) {
      return false;
    }
  }
}
