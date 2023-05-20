package com.ksaraev.suddenrun.user;

import lombok.experimental.StandardException;

@StandardException
public class SuddenrunUserIsNotRegisteredException extends RuntimeException {

  public SuddenrunUserIsNotRegisteredException(String userId) {
    super("User with id" + " [" + userId + "] is not registered");
  }
}
