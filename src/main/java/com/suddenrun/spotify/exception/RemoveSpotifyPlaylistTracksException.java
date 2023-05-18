package com.suddenrun.spotify.exception;

import lombok.experimental.StandardException;

@StandardException
public class RemoveSpotifyPlaylistTracksException extends SpotifyPlaylistServiceException {

  private static final String ERROR_WHILE_REMOVING_SPOTIFY_PLAYLIST_TRACKS =
      "Error while removing Spotify playlist tracks";

  public RemoveSpotifyPlaylistTracksException(String spotifyPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_REMOVING_SPOTIFY_PLAYLIST_TRACKS
            + " from playlist with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
