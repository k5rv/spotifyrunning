package com.ksaraev.spotifyrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class AppSpotifyServiceInteractionException extends RuntimeException {

  private static final String ERROR_WHILE_INTERACTING_WITH_SPOTIFY_SERVICE =
      "Error while interacting with spotify service";

  public AppSpotifyServiceInteractionException(Throwable cause) {
    super(ERROR_WHILE_INTERACTING_WITH_SPOTIFY_SERVICE + ": " + cause.getMessage(), cause);
  }
}
