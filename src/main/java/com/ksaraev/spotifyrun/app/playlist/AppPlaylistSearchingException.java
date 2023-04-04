package com.ksaraev.spotifyrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistSearchingException extends RuntimeException {

  private static final String ERROR_WHILE_SEARCHING_PLAYLIST = "Error while searching playlist";

  public AppPlaylistSearchingException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_SEARCHING_PLAYLIST
            + " that belongs to user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }

  public AppPlaylistSearchingException(String appPlaylistId) {
    super(ERROR_WHILE_SEARCHING_PLAYLIST + " with id [" + appPlaylistId + "]");
  }
}
