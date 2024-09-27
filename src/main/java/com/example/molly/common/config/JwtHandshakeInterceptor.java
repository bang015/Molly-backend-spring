package com.example.molly.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import com.example.molly.auth.security.JwtTokenProvider;
import java.util.Map;

public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean beforeHandshake(
      @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
      @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {

    String token = request.getHeaders().getFirst("Authorization");

    if (token != null && jwtTokenProvider.validateToken(token)) {

      Long userId = jwtTokenProvider.getUserIdFromToken(token);
      attributes.put("userId", userId);
      return super.beforeHandshake(request, response, wsHandler, attributes);
    } else {
      response.setStatusCode(HttpStatus.FORBIDDEN);
      return false;
    }
  }
}
