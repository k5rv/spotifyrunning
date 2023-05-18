package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetSpotifyUserPlaylistsException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_SPOTIFY_USER_PLAYLISTS =
      "Error while getting Spotify user playlists";

  public GetSpotifyUserPlaylistsException(String spotifyUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_SPOTIFY_USER_PLAYLISTS
            + " for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
