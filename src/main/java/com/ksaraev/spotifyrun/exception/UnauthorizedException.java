package com.ksaraev.spotifyrun.exception;

import lombok.experimental.StandardException;

@StandardException
public class UnauthorizedException extends ApplicationException {
  public static final String UNAUTHORIZED = "Unauthorized";
}
