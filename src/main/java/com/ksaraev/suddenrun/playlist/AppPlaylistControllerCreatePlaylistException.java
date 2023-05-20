package com.ksaraev.suddenrun.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistControllerCreatePlaylistException extends RuntimeException {

  private static final String ERROR_WHILE_CREATING_RUNNING_WORKOUT_PLAYLIST =
      "Error while creating running workout playlist";

  public AppPlaylistControllerCreatePlaylistException(Throwable cause) {
    super(ERROR_WHILE_CREATING_RUNNING_WORKOUT_PLAYLIST + ": " + cause.getMessage(), cause);
  }
}
