package com.ksaraev.spotifyrun.exception.service;

import lombok.experimental.StandardException;

@StandardException
public class GetUserException extends RuntimeException {
  public static final String UNABLE_TO_GET_USER = "Unable to get user: ";
}
