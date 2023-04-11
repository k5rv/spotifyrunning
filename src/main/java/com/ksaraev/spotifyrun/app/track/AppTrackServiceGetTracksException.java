package com.ksaraev.spotifyrun.app.track;

import lombok.experimental.StandardException;

@StandardException
public class AppTrackServiceGetTracksException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_TRACKS = "Error while getting tracks";

  public AppTrackServiceGetTracksException(Throwable cause) {
    super(ERROR_WHILE_GETTING_TRACKS + ": " + cause.getMessage(), cause);
  }
}
