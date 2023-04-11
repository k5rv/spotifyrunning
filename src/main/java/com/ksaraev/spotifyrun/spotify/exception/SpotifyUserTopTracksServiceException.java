package com.ksaraev.spotifyrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyUserTopTracksServiceException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS =
      "Error while getting spotify user top tracks";

  public SpotifyUserTopTracksServiceException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS + ": " + cause.getMessage(), cause);
  }
}
