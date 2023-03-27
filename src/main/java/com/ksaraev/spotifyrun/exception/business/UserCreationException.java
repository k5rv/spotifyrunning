package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class UserCreationException extends RuntimeException {
  public static final String UNABLE_TO_CREATE_USER = "Unable to create user: ";
}
