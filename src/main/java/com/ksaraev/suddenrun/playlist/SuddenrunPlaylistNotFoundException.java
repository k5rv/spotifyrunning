package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunPlaylistNotFoundException extends RuntimeException {

  public SuddenrunPlaylistNotFoundException(String appUserId) {
    super("User with id [" + appUserId + "] doesn't have any playlists");
  }
}
