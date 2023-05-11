package com.suddenrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class AppUserAlreadyRegisteredException extends RuntimeException {

  public AppUserAlreadyRegisteredException(String appUserId) {
    super("User with id" + " [" + appUserId + "] already registered");
  }
}
