package com.ksaraev.spotifyrun.spotify.exception.refactored;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyPlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST =
      "Error while getting spotify playlist";

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
