package com.ksaraev.spotifyrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistAddTracksException extends RuntimeException {

  private static final String ERROR_WHILE_ADDING_TRACKS = "Error while adding tracks";

  public AppPlaylistAddTracksException(String appPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_ADDING_TRACKS
            + " to playlist with id ["
            + appPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
