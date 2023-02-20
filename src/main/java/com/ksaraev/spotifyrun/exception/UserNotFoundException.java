package com.ksaraev.spotifyrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class UserNotFoundException extends ApplicationException {
  public static final String USER_NOT_FOUND = "User not found: ";
}
