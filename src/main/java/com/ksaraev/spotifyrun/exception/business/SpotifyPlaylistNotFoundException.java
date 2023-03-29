package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class SpotifyPlaylistNotFoundException extends RuntimeException {

  public SpotifyPlaylistNotFoundException(String playlistId, Throwable cause) {
    super("Spotify playlist with id [" + playlistId + "] not found", cause);
  }
}
