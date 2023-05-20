package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistServicePlaylistDoesNotExistException extends RuntimeException {

  public AppPlaylistServicePlaylistDoesNotExistException(String appPlaylistId) {
    super("Playlist with id [" + appPlaylistId + "] doesn't exist");
  }
}
