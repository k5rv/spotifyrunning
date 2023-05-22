package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class CreateSuddenrunPlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_CREATING_PLAYLIST = "Error while creating playlist";

  public CreateSuddenrunPlaylistException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_CREATING_PLAYLIST
            + " for user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
