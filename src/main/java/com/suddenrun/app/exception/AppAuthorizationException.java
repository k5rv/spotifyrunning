package com.suddenrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class AppAuthorizationException extends RuntimeException {

  private static final String APP_AUTHORIZATION_ERROR = "App authorization error";

  public AppAuthorizationException(Throwable cause) {
    super(APP_AUTHORIZATION_ERROR + ": " + cause.getMessage(), cause);
  }
}
