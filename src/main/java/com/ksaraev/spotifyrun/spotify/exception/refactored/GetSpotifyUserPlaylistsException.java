package com.ksaraev.spotifyrun.spotify.exception.refactored;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyUserPlaylistsException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST =
      "Error while getting spotify playlists";

  public GetSpotifyUserPlaylistsException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_PLAYLIST
            + " for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
