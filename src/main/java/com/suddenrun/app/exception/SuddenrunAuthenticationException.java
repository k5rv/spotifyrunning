package com.suddenrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunAuthenticationException extends RuntimeException {

  private static final String SUDDENRUN_AUTHENTICATION_EXCEPTION =
      "Suddenrun authentication exception";

  public SuddenrunAuthenticationException(Throwable cause) {
    super(SUDDENRUN_AUTHENTICATION_EXCEPTION + ": " + cause.getMessage(), cause);
  }
}
