package com.ksaraev.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyAccessTokenException extends RuntimeException {

  private static final String BAD_OR_EXPIRED_ACCESS_TOKEN =
      "Bad or expired access token";

  public SpotifyAccessTokenException(Throwable cause) {
    super(BAD_OR_EXPIRED_ACCESS_TOKEN + ": " + cause.getMessage(), cause);
  }
}
