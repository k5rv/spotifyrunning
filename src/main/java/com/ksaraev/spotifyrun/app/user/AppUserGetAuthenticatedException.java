package com.ksaraev.spotifyrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserGetAuthenticatedException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_AUTHENTICATED_USER =
      "Error while getting authenticated user";

  public AppUserGetAuthenticatedException(Throwable cause) {
    super(ERROR_WHILE_GETTING_AUTHENTICATED_USER + ": " + cause.getMessage(), cause);
  }

  public AppUserGetAuthenticatedException() {
    super(ERROR_WHILE_GETTING_AUTHENTICATED_USER);
  }
}
