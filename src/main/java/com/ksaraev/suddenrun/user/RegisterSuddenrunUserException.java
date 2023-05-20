package com.ksaraev.suddenrun.user;

import lombok.experimental.StandardException;

@StandardException
public class RegisterSuddenrunUserException extends RuntimeException {

  private static final String ERROR_WHILE_REGISTERING_USER = "Error while registering user with id";

  public RegisterSuddenrunUserException(String userId, Throwable cause) {
    super(ERROR_WHILE_REGISTERING_USER + " [" + userId + "]: " + cause.getMessage(), cause);
  }
}
