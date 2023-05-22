package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistNotFoundException extends RuntimeException {

  public AppPlaylistNotFoundException(String appUserId) {
    super("User with id [" + appUserId + "] doesn't have any playlists");
  }
}
