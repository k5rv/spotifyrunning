package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyPlaylistServiceGetUserPlaylistsException
    extends SpotifyPlaylistServiceException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST =
      "Error while getting spotify playlists";

  public SpotifyPlaylistServiceGetUserPlaylistsException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST
            + " for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
