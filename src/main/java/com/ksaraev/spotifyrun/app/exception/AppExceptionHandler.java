package com.ksaraev.spotifyrun.app.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {

  @ExceptionHandler(value = {AppAuthorizationException.class})
  public ResponseEntity<AppError> handleAuthenticationException(AppAuthorizationException e) {
    log.error(e.getMessage(), e);
    String message = "Error during Spotify authorization";
    int unauthorized = UNAUTHORIZED.value();
    return ResponseEntity.status(UNAUTHORIZED).body(new AppError(unauthorized, message));
  }

  @ExceptionHandler(value = {AppUserNotRegisteredException.class})
  public ResponseEntity<AppError> handleAppUserNotRegisteredException(
      AppUserNotRegisteredException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new AppError(notFound, message));
  }

  @ExceptionHandler(value = {AppUserAlreadyRegisteredException.class})
  public ResponseEntity<AppError> handleAppUserAlreadyRegisteredException(
      AppUserAlreadyRegisteredException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new AppError(conflict, message));
  }

  @ExceptionHandler(value = {AppPlaylistNotFoundException.class})
  public ResponseEntity<AppError> handleAppPlaylistNotFoundException(
      AppPlaylistNotFoundException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new AppError(notFound, message));
  }

  @ExceptionHandler(value = {AppPlaylistAlreadyExistException.class})
  public ResponseEntity<AppError> handleAppPlaylistAlreadyExistException(
      AppPlaylistAlreadyExistException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new AppError(conflict, message));
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
