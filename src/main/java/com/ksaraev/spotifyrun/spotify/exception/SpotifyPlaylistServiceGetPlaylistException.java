package com.ksaraev.spotifyrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyPlaylistServiceGetPlaylistException extends SpotifyPlaylistServiceException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST =
      "Error while getting spotify playlist";

  public SpotifyPlaylistServiceGetPlaylistException(String spotifyPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST
            + " with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
