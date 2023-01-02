package com.ksaraev.spotifyrunning.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class SpotifyRunningApplicationExceptionHandler {

  @ExceptionHandler(value = {SpotifyResourceNotFoundException.class})
  public ResponseEntity<Object> handleSpotifyResourceNotFoundException(
      SpotifyResourceNotFoundException e) {
    SpotifyRunningApplicationException spotifyRunningApplicationException =
        new SpotifyRunningApplicationException(
            e.getMessage(), HttpStatus.NOT_FOUND, ZonedDateTime.now());
    log.error(e.getMessage(), spotifyRunningApplicationException);
    return new ResponseEntity<>(spotifyRunningApplicationException, HttpStatus.NOT_FOUND);
  }
}
