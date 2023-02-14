package com.ksaraev.spotifyrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class GetUserException extends ApplicationException {
  public static final String GET_USER_ERROR_MESSAGE = "Unable to get user";
}
