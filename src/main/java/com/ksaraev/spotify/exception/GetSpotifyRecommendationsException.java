package com.ksaraev.spotify.exception;


public class GetSpotifyRecommendationsException extends SpotifyServiceException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_RECOMMENDATIONS =
      "Error while getting Spotify user recommendations";

  public GetSpotifyRecommendationsException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_USER_RECOMMENDATIONS + ": " + cause.getMessage(), cause);
  }
}
