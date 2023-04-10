package com.ksaraev.spotifyrun.spotify.exception.refactored;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyUserProfileException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE =
      "Error while getting current spotify user profile";
  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_PROFILE =
      "Error while getting spotify user profile";

  public GetSpotifyUserProfileException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_USER_PROFILE
            + " with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }

  public GetSpotifyUserProfileException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE + ": " + cause.getMessage(), cause);
  }
}
