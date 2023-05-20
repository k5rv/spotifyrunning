package com.suddenrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class GetSuddenrunUserException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_USER_WITH_ID = "Error while getting user with id";

  public GetSuddenrunUserException(String userId, Throwable cause) {
    super(ERROR_WHILE_GETTING_USER_WITH_ID + " [" + userId + "]: " + cause.getMessage(), cause);
  }
}
