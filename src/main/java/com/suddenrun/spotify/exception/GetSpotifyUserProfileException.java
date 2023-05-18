package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyUserProfileException extends RuntimeException {
  private static final String ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE =
      "Error while getting current spotify user profile";

  public GetSpotifyUserProfileException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE + ": " + cause.getMessage(), cause);
  }
}
