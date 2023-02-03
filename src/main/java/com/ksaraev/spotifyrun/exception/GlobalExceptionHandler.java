package com.ksaraev.spotifyrun.exception;

import com.ksaraev.spotifyrun.client.exception.SpotifyClientException;
import com.ksaraev.spotifyrun.client.exception.http.SpotifyException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    log.error("Spotifyrun exception occurred", e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }

  @ExceptionHandler(value = {SpotifyException.class})
  public ResponseEntity<ErrorResponse> handleSpotifyException(SpotifyException e) {
    log.error("Spotify Web Api exception occurred", e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }

  @ExceptionHandler(value = {SpotifyClientException.class})
  public ResponseEntity<ErrorResponse> handleSpotifyClientException(SpotifyClientException e) {
    log.error("Spotify client exception occurred", e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }

  @ExceptionHandler(value = {ValidationException.class})
  public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
    log.error("Validation exception occurred", e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }
}
