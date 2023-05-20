package com.ksaraev.spotify.exception;


public class GetSpotifyUserPlaylistsException extends SpotifyServiceException {

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
