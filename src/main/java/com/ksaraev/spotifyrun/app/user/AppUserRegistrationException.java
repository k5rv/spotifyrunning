package com.ksaraev.spotifyrun.app.user;

import lombok.experimental.StandardException;

@StandardException
public class AppUserRegistrationException extends RuntimeException {

  public AppUserRegistrationException(String appUserId, Throwable cause) {
    super("Error while registering user with id [" + appUserId + "]: " + cause.getMessage(), cause);
  }
}
