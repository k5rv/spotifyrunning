package com.ksaraev.spotifyrun.app.playlist;

import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class PlaylistExceptionHandler {

  @ExceptionHandler(
      value = {UserTopTracksNotFoundException.class, RecommendationsNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleBusinessException(
      RuntimeException e) {
    log.error(UNABLE_TO_CREATE_PLAYLIST + e.getMessage(), e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage(), ZonedDateTime.now()));
  }

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    log.error(UNABLE_TO_CREATE_PLAYLIST + e.getMessage(), e);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(INTERNAL_SERVER_ERROR, ZonedDateTime.now()));
  }
@Data
public static final class ErrorResponse {
  private Integer status;
  private String message;
  private ZonedDateTime timeStamp;

  public ErrorResponse(HttpStatus httpStatus, ZonedDateTime timeStamp) {
    this.status = httpStatus.value();
    this.message = httpStatus.getReasonPhrase();
    this.timeStamp = timeStamp;
  }

  public ErrorResponse(HttpStatus httpStatus, String message, ZonedDateTime timeStamp) {
    this.status = httpStatus.value();
    this.message = message;
    this.timeStamp = timeStamp;
  }
}}
