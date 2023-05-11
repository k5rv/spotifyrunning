package com.suddenrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistServicePlaylistDoesNotExistException extends RuntimeException {

  public AppPlaylistServicePlaylistDoesNotExistException(String appPlaylistId) {
    super("Playlist with id [" + appPlaylistId + "] doesn't exist");
  }
}
