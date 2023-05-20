package com.ksaraev.suddenrun.user;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserIsAlreadyRegisteredException extends RuntimeException {

  public SuddenrunUserIsAlreadyRegisteredException(String userId) {
    super("User with id" + " [" + userId + "] already registered");
  }
}
