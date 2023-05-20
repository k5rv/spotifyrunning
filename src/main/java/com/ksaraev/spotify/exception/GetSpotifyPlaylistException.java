package com.ksaraev.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyPlaylistException extends SpotifyPlaylistServiceException {

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
