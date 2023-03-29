package com.ksaraev.spotifyrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserRegistrationStatusSearchingException extends RuntimeException {

  public AppUserRegistrationStatusSearchingException(String appUserid, Throwable cause) {
    super(
        "Error while searching registration status for user with id ["
            + appUserid
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
