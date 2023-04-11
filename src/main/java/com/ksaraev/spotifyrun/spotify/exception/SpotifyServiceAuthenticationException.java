package com.ksaraev.spotifyrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyServiceAuthenticationException extends RuntimeException {

  private static final String SPOTIFY_SERVICE_AUTHENTICATION_ERROR =
      "Spotify service authentication error while performing operation";

  public SpotifyServiceAuthenticationException(Throwable cause) {
    super(SPOTIFY_SERVICE_AUTHENTICATION_ERROR + ": ", cause);
  }
}
