package com.ksaraev.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class AddSpotifyPlaylistTracksExceptions extends RuntimeException {

  private static final String ERROR_WHILE_ADDING_SPOTIFY_PLAYLIST_TRACKS =
      "Error while adding Spotify playlist tracks";

  public AddSpotifyPlaylistTracksExceptions(String spotifyPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_ADDING_SPOTIFY_PLAYLIST_TRACKS
            + " to playlist with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
