package com.suddenrun.app.playlist;

import lombok.experimental.StandardException;

@StandardException
public class AppPlaylistServiceTracksRevisionException extends RuntimeException {

  private static final String ERROR_WHILE_REVISING_TRACKS = "Error while revising tracks";

  public AppPlaylistServiceTracksRevisionException(String appPlaylistId, Throwable cause) {
    super(
        ERROR_WHILE_REVISING_TRACKS
            + " playlist id: ["
            + appPlaylistId
            + "]"
            + " :"
            + cause.getMessage());
  }
}
