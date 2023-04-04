package com.ksaraev.spotifyrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistSpotifySourceNotFoundException extends RuntimeException {

  public AppPlaylistSpotifySourceNotFoundException(String appPlaylistId) {
    super("Playlist with id [" + appPlaylistId + "] not found in Spotify");
  }
}
