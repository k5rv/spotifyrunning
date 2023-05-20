package com.ksaraev.spotify.exception;


public class GetSpotifyPlaylistException extends SpotifyServiceException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST =
      "Error while getting Spotify playlist";

  public GetSpotifyPlaylistException(String spotifyPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST
            + " with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
