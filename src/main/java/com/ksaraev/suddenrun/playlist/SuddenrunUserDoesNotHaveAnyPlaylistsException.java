package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserDoesNotHaveAnyPlaylistsException extends RuntimeException {

  public SuddenrunUserDoesNotHaveAnyPlaylistsException(String appUserId) {
    super("User with id [" + appUserId + "] doesn't have any playlists");
  }
}
