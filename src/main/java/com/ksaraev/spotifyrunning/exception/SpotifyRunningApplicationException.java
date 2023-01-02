package com.ksaraev.spotifyrunning.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record SpotifyRunningApplicationException(
    String message, HttpStatus httpStatus, ZonedDateTime zonedDateTime) {}
