package com.ksaraev.spotifyrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistDoesNotExistException extends RuntimeException {

  public AppPlaylistDoesNotExistException(String appUserId) {
    super("Playlist with id [" + appUserId + "] doesn't exist");
  }
}
