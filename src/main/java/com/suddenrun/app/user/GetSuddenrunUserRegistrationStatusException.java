package com.suddenrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class GetSuddenrunUserRegistrationStatusException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_REGISTRATION_STATUS =
      "Error while getting registration status for user with id";

  public GetSuddenrunUserRegistrationStatusException(String userId, Throwable cause) {
    super(
        ERROR_WHILE_GETTING_REGISTRATION_STATUS + " [" + userId + "]: " + cause.getMessage(),
        cause);
  }
}
