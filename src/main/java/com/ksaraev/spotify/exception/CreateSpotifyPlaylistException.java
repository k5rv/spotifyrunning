package com.ksaraev.spotify.exception;


public class CreateSpotifyPlaylistException extends SpotifyServiceException {

  private static final String ERROR_WHILE_CREATING_SPOTIFY_PLAYLIST =
      "Error while creating Spotify playlist";

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
