package com.suddenrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserServiceGetAuthenticatedUserException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_AUTHENTICATED_USER =
      "Error while getting authenticated user";

  public AppUserServiceGetAuthenticatedUserException(Throwable cause) {
    super(ERROR_WHILE_GETTING_AUTHENTICATED_USER + ": " + cause.getMessage(), cause);
  }

  public AppUserServiceGetAuthenticatedUserException() {
    super(ERROR_WHILE_GETTING_AUTHENTICATED_USER);
  }
}
