package com.ksaraev.spotifyrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyPlaylistServiceAddTracksException extends SpotifyPlaylistServiceException {

  private static final String ERROR_WHILE_ADDING_SPOTIFY_PLAYLIST_TRACKS =
      "Error while adding spotify playlist tracks";

  public SpotifyPlaylistServiceAddTracksException(String spotifyPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_ADDING_SPOTIFY_PLAYLIST_TRACKS
            + " to playlist with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
