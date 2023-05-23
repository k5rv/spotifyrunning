package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunPlaylistDoesNotExistException extends RuntimeException {

  public SuddenrunPlaylistDoesNotExistException(String playlistId) {
    super("Playlist with id [" + playlistId + "] doesn't exist");
  }
}
