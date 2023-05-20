package com.ksaraev.suddenrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunSpotifyInteractionException extends RuntimeException {

  private static final String ERROR_WHILE_INTERACTING_WITH_SPOTIFY_SERVICE =
      "Error while interacting with spotify service";

  public SuddenrunSpotifyInteractionException(Throwable cause) {
    super(ERROR_WHILE_INTERACTING_WITH_SPOTIFY_SERVICE + ": " + cause.getMessage(), cause);
  }
}
