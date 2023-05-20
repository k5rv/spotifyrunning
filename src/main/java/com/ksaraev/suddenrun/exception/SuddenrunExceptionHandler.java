package com.ksaraev.suddenrun.exception;

import static org.springframework.http.HttpStatus.*;

import com.ksaraev.suddenrun.user.SuddenrunUserIsAlreadyRegisteredException;
import com.ksaraev.suddenrun.user.SuddenrunUserIsNotRegisteredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SuddenrunExceptionHandler {

  @ExceptionHandler(value = {SuddenrunAuthenticationException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunAuthenticationException(
      SuddenrunAuthenticationException e) {
    log.error(e.getMessage());
    int unauthorized = UNAUTHORIZED.value();
    String message = "Suddenrun authentication error";
    return ResponseEntity.status(UNAUTHORIZED).body(new SuddenrunError(unauthorized, message));
  }

  @ExceptionHandler(value = {SuddenrunUserIsNotRegisteredException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserIsNotRegisteredException(
      SuddenrunUserIsNotRegisteredException e) {
    log.error(e.getMessage());
    int notFound = NOT_FOUND.value();
    String message = "Suddenrun user is not registered";
    return ResponseEntity.status(NOT_FOUND).body(new SuddenrunError(notFound, message));
  }

  @ExceptionHandler(value = {SuddenrunUserIsAlreadyRegisteredException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserIsAlreadyRegisteredException(
      SuddenrunUserIsAlreadyRegisteredException e) {
    log.error(e.getMessage());
    int conflict = CONFLICT.value();
    String message = "Suddenrun user is already registered";
    return ResponseEntity.status(CONFLICT).body(new SuddenrunError(conflict, message));
  }

  @ExceptionHandler(value = {AppPlaylistNotFoundException.class})
  public ResponseEntity<SuddenrunError> handleAppPlaylistNotFoundException(
      AppPlaylistNotFoundException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new SuddenrunError(notFound, message));
  }

  @ExceptionHandler(value = {AppPlaylistAlreadyExistException.class})
  public ResponseEntity<SuddenrunError> handleAppPlaylistAlreadyExistException(
      AppPlaylistAlreadyExistException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new SuddenrunError(conflict, message));
  }

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<SuddenrunError> handleRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    String message = "Suddenrun internal error";
    int internalServerError = INTERNAL_SERVER_ERROR.value();
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new SuddenrunError(internalServerError, message));
  }
}
