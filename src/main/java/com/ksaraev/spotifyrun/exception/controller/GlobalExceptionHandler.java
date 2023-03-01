package com.ksaraev.spotifyrun.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  private static final String SPOTIFYRUN_EXCEPTION_MESSAGE = "Spotifyrun exception occurred: ";

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    log.error(SPOTIFYRUN_EXCEPTION_MESSAGE + e.getMessage(), e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }
}
