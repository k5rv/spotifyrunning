package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistServiceGetPlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_PLAYLIST = "Error while getting playlist";

  public AppPlaylistServiceGetPlaylistException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_PLAYLIST
            + " that belongs to user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
