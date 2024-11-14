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
// 웹소켓 메시지 전송 시 요청을 가로체 토큰을 검증
public class JwtChannelInterceptor implements ChannelInterceptor {
  private final JwtTokenProvider jwtTokenProvider;

  @SuppressWarnings("null")
  @Override
  // preSend는 메시지가 웹소켓 채널로 전송되기 전에 호출된다
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    // 메시지의 헤더에 접근할 수 있도록 StompHeaderAccessor 객체 생성
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    // 헤더에서 토큰 추출
    String token = accessor.getFirstNativeHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    // 토큰을 검증하고 토큰에서 userId를 추출해 세션에 저장
    if (token != null && jwtTokenProvider.validateToken(token)) {
      Long userId = jwtTokenProvider.getUserIdFromToken(token);
      accessor.getSessionAttributes().put("userId", userId);
    }

    return message;
  }
}
