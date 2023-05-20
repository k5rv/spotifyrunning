package com.ksaraev.spotify.exception;


public class GetSpotifyUserTopTracksException extends SpotifyServiceException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS =
      "Error while getting Spotify user top tracks";

  public GetSpotifyUserTopTracksException(Throwable cause) {
    super(ERROR_WHILE_GETTING_SPOTIFY_USER_TOP_TRACKS + ": " + cause.getMessage(), cause);
  }
}
