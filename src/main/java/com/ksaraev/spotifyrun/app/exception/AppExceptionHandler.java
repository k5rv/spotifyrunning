package com.ksaraev.spotifyrun.app.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.ksaraev.spotifyrun.app.playlist.AppPlaylistControllerCreatePlaylistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

  @ExceptionHandler(value = {AppAuthenticationException.class})
  public ResponseEntity<AppError> handleAuthenticationException(AppAuthenticationException e) {
    log.error(e.getMessage(), e);
    int unauthorized = UNAUTHORIZED.value();
    String message = "Application authentication error";
    return ResponseEntity.status(UNAUTHORIZED).body(new AppError(unauthorized, message));
  }

  @ExceptionHandler(value = {AppPlaylistControllerCreatePlaylistException.class})
  public ResponseEntity<AppError> handleAppPlaylistControllerCreatePlaylistException(
      AppPlaylistControllerCreatePlaylistException e) {
    log.error(e.getMessage(), e);
    int internalServerError = INTERNAL_SERVER_ERROR.value();
    String message = "Application error while creating playlist";
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new AppError(internalServerError, message));
  }

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<AppError> handleRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    int internalServerError = INTERNAL_SERVER_ERROR.value();
    String message = "Application internal error";
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new AppError(internalServerError, message));
  }
}
