package com.ksaraev.suddenrun.track;

import lombok.experimental.StandardException;

@StandardException
public class GetSuddenrunTracksException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_TRACKS = "Error while getting tracks";

  public GetSuddenrunTracksException(Throwable cause) {
    super(ERROR_WHILE_GETTING_TRACKS + ": " + cause.getMessage(), cause);
  }
}
