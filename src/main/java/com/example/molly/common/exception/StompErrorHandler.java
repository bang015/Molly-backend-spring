package com.example.molly.common.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.example.molly.common.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StompErrorHandler extends StompSubProtocolErrorHandler {
  private final ObjectMapper objectMapper;

  @SuppressWarnings("null")
  @Override
  public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
    if (ex instanceof MessageDeliveryException) {
      Throwable cause = ex.getCause();
      if (cause instanceof AccessDeniedException) {
        return sendErrorMessage(new ErrorResponse("Access denied", 1001));
      }
      if (cause instanceof ExpiredJwtException) {
        return sendErrorMessage(new ErrorResponse("Token has expired", 4001));
      }
      if (cause instanceof JwtException) {
        return sendErrorMessage(new ErrorResponse("Invalid JWT token", 1003));
      }
    }
    return super.handleClientMessageProcessingError(clientMessage, ex);
  }

  private Message<byte[]> sendErrorMessage(ErrorResponse errorResponse) {
    StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
    headers.setMessage(errorResponse.getMessage());
    headers.setLeaveMutable(true);

    try {
      String json = objectMapper.writeValueAsString(errorResponse);
      return MessageBuilder.createMessage(json.getBytes(StandardCharsets.UTF_8),
          headers.getMessageHeaders());
    } catch (JsonProcessingException e) {
      log.error("Failed to convert ErrorResponse to JSON", e);
      return MessageBuilder.createMessage(errorResponse.getMessage().getBytes(StandardCharsets.UTF_8),
          headers.getMessageHeaders());
    }
  }
}
