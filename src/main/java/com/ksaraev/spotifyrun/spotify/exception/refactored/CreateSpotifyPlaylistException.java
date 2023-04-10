package com.ksaraev.spotifyrun.spotify.exception.refactored;

import lombok.experimental.StandardException;

@StandardException
public class CreateSpotifyPlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_CREATING_SPOTIFY_PLAYLIST =
      "Error while creating spotify playlist";

  public CreateSpotifyPlaylistException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_CREATING_SPOTIFY_PLAYLIST
            + " for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
