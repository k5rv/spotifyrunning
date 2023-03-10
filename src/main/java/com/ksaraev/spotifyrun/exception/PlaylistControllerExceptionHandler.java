package com.ksaraev.spotifyrun.exception;

import static com.ksaraev.spotifyrun.exception.business.CreatePlaylistException.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.ksaraev.spotifyrun.exception.business.RecommendationsNotFoundException;
import com.ksaraev.spotifyrun.exception.business.UserTopTracksNotFoundException;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class PlaylistControllerExceptionHandler {

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
}
