package com.suddenrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserServiceGetUserRegistrationStatusException extends RuntimeException {

  public AppUserServiceGetUserRegistrationStatusException(String appUserid, Throwable cause) {
    super(
        "Error while getting registration status for user with id ["
            + appUserid
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
