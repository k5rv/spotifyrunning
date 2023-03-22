package com.ksaraev.spotifyrun.exception.business;

import lombok.experimental.StandardException;

@StandardException
public class GetAppUserException extends RuntimeException {
  public static final String UNABLE_TO_GET_APP_USER = "Unable to get app user: ";
}
