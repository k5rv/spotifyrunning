package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyRecommendationsException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_RECOMMENDATIONS =
      "Error while getting Spotify user recommendations";

  public GetSpotifyRecommendationsException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_USER_RECOMMENDATIONS + ": " + cause.getMessage(), cause);
  }
}
