package com.example.molly.auth.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
  private final JwtTokenProvider jwtTokenProvider;

  @SuppressWarnings("null")
  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    String token = accessor.getFirstNativeHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    if (token != null && jwtTokenProvider.validateToken(token)) {

      Long userId = jwtTokenProvider.getUserIdFromToken(token);
      accessor.getSessionAttributes().put("userId", userId);
    }

    return message;
  }
}
