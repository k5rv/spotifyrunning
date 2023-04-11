package com.ksaraev.spotifyrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class AppAuthenticationException extends RuntimeException {

  private static final String APP_AUTHENTICATION_ERROR = "App authentication error";

  public AppAuthenticationException(Throwable cause) {
    super(APP_AUTHENTICATION_ERROR + ": " + cause.getMessage(), cause);
  }
}
