package com.ksaraev.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyUserTopTracksException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS =
      "Error while getting Spotify user top tracks";

  public GetSpotifyUserTopTracksException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS + ": " + cause.getMessage(), cause);
  }
}
