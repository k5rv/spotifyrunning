package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class GetSuddenrunPlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_PLAYLIST = "Error while getting playlist";

  public GetSuddenrunPlaylistException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_PLAYLIST
            + " for user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
