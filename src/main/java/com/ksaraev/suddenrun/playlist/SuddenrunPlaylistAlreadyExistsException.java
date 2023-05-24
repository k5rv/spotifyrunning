package com.ksaraev.suddenrun.playlist;

public class SuddenrunPlaylistAlreadyExistsException extends RuntimeException {

  public SuddenrunPlaylistAlreadyExistsException(String playlistId) {
    super("Playlist with id [" + playlistId + "] already exists");
  }
}
