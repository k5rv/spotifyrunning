package com.ksaraev.spotifyrun.spotify.exception.refactored;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyRecommendationsException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_RECOMMENDATIONS =
      "Error while getting spotify recommendations";

  public GetSpotifyRecommendationsException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_RECOMMENDATIONS + ": " + cause.getMessage(), cause);
  }
}
