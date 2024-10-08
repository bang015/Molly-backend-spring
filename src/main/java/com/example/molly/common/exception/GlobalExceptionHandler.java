package com.example.molly.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.molly.common.dto.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    ErrorResponse errorResponse = new ErrorResponse("이메일 또는 비밀번호가 잘못되었습니다.", HttpStatus.FORBIDDEN.value());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
    ErrorResponse errorResponse = new ErrorResponse("기간이 만료된 토큰입니다.", HttpStatus.UNAUTHORIZED.value());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler({ JwtException.class, SignatureException.class })
  public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
    ErrorResponse errorResponse = new ErrorResponse("잘못된 토큰입니다.", HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
