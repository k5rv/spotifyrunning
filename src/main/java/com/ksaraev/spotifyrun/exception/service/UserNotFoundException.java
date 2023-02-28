package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class UserNotFoundException extends ServiceException {
  public static final String USER_NOT_FOUND = "User not found: ";
}
