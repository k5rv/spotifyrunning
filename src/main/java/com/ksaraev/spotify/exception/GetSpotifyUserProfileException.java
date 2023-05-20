package com.ksaraev.spotify.exception;

public class GetSpotifyUserProfileException extends SpotifyServiceException {
  private static final String ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE =
      "Error while getting current Spotify user profile";

  public GetSpotifyUserProfileException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_CURRENT_USER_PROFILE + ": " + cause.getMessage(), cause);
  }
}
