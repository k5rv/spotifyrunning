package com.suddenrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserIsAlreadyRegisteredException extends RuntimeException {

  public SuddenrunUserIsAlreadyRegisteredException(String appUserId) {
    super("User with id" + " [" + appUserId + "] already registered");
  }
}
