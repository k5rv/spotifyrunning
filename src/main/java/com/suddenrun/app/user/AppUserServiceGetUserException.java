package com.suddenrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserServiceGetUserException extends RuntimeException {

  private static final String ERROR_WHILE_GETTING_USER_WITH_ID = "Error while getting user with id";

  public AppUserServiceGetUserException(String appUserId, Throwable cause) {
    super(ERROR_WHILE_GETTING_USER_WITH_ID + " [" + appUserId + "]: " + cause.getMessage(), cause);
  }
}
