package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyAccessTokenException extends RuntimeException {

  private static final String BAD_OR_EXPIRED_ACCESS_TOKEN_REAUTHENTICATE_THE_USER =
      "Bad or expired access token. Reauthenticate the user: ";

  public SpotifyAccessTokenException(Throwable cause) {
    super(BAD_OR_EXPIRED_ACCESS_TOKEN_REAUTHENTICATE_THE_USER + ": " + cause.getMessage(), cause);
  }
}
