package com.ksaraev.spotifyrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyPlaylistServiceCreatePlaylistException extends SpotifyPlaylistServiceException {

  private static final String ERROR_WHILE_CREATING_SPOTIFY_PLAYLIST =
      "Error while creating spotify playlist";

  public SpotifyPlaylistServiceCreatePlaylistException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_CREATING_SPOTIFY_PLAYLIST
            + " for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
