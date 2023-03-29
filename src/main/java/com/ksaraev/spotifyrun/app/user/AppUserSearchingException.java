package com.ksaraev.spotifyrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserSearchingException extends RuntimeException {

  private static final String ERROR_WHILE_SEARCHING_USER_WITH_ID =
      "Error while searching user with id";

  public AppUserSearchingException(String appUserId, Throwable cause) {
    super(
        ERROR_WHILE_SEARCHING_USER_WITH_ID + " [" + appUserId + "]: " + cause.getMessage(), cause);
  }

  public AppUserSearchingException(String appUserId) {
    super(ERROR_WHILE_SEARCHING_USER_WITH_ID + " [" + appUserId + "]");
  }
}
