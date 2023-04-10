package com.ksaraev.spotifyrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistGettingException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_PLAYLIST = "Error while getting playlist";

  public AppPlaylistGettingException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_PLAYLIST
            + " that belongs to user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
